package com.example.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.example.location.databinding.ActivityLocationBinding
import com.example.location.domain.TerminalsDomainModel
import com.example.location.presentation.BottomSheetFragment
import com.example.location.utils.LocationPermissionHelper
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.interpolate
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.PuckBearingSource
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.locationcomponent.location2
import java.lang.ref.WeakReference
import java.text.NumberFormat

class LocationActivity : AppCompatActivity() {
    private lateinit var locationPermissionHelper: LocationPermissionHelper
    private lateinit var binding: ActivityLocationBinding
    private lateinit var model: TerminalsDomainModel

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        Log.d(this.javaClass.simpleName, "point: $it")
        // Create a polygon

        val triangleCoordinates =
            mutableListOf(
                Point.fromLngLat(model.origin!!.long, model.origin!!.lat),
                it,
        )

        model.destinations.onEach { destination ->
            triangleCoordinates.add(Point.fromLngLat(destination.long, destination.lat))
        }
// Convert to a camera options from a given geometry and padding
        val cameraPositionCoor = mapView.getMapboxMap().cameraForCoordinates(triangleCoordinates, EdgeInsets(1.0, 55.0, 20.0, 55.0))
// Set camera position
        mapView.getMapboxMap().setCamera(cameraPositionCoor)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }
    private lateinit var mapView: MapView
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(this.javaClass.simpleName, "From: onCreate called")
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapView = binding.mapView
        locationPermissionHelper = LocationPermissionHelper(WeakReference(this))
        locationPermissionHelper.checkPermissions {
            onMapReady()
        }

        val i: Intent = intent
        model = i.getParcelableExtra<TerminalsDomainModel>("response") as TerminalsDomainModel
        Log.d(this.javaClass.simpleName, "From model: $model")
        val ft = supportFragmentManager.beginTransaction()
        val fragment = BottomSheetFragment.newInstance(model)
        ft.replace(R.id.bottomSheet, fragment)
        ft.commit()
        onMapReady(model)

        val myFormat = NumberFormat.getInstance().apply { isGroupingUsed = true }
        binding.priceButton.text = getString(R.string.rial, myFormat.format(model.price))
    }


    private fun onMapReady(model: TerminalsDomainModel? = null) {

        mapView.getMapboxMap().loadStyleUri(
            Style.MAPBOX_STREETS
        ) {
            initLocationComponent()
            setupGesturesListener()
            addAnnotationToMap(model?.origin?.long ?: 0.0, model?.origin?.lat ?: 0.0, 0)
            model?.destinations?.onEach {
                addAnnotationToMap(it.long, it.lat, it.destNumber)
            }
        }
    }

    private fun setupGesturesListener() {
        mapView.gestures.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    this@LocationActivity,
                    com.mapbox.maps.R.drawable.mapbox_user_bearing_icon,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    this@LocationActivity,
                    com.mapbox.maps.R.drawable.mapbox_mylocation_icon_bearing,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.location2.puckBearingSource = PuckBearingSource.HEADING
    }

    private fun onCameraTrackingDismissed() {
        Toast.makeText(this, "onCameraTrackingDismissed", Toast.LENGTH_SHORT).show()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }

    private fun addAnnotationToMap(long: Double, lat: Double, destNumber: Int) {

        val resId = when(destNumber) {
            0 -> R.drawable.ic_top
            1 -> R.drawable.ic_dest_one
            2 -> R.drawable.ic_dest_two
            else -> R.drawable.red_marker
        }
// Create an instance of the Annotation API and get the PointAnnotationManager.
        bitmapFromDrawableRes(
            this,
            resId
        )?.let {
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager(mapView)
// Set options for the resulting symbol layer.
            val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
// Define a geographic coordinate.
                .withPoint(Point.fromLngLat(long, lat))
// Specify the bitmap you assigned to the point annotation
// The bitmap will be added to map style automatically.
                .withIconImage(it)
// Add the resulting pointAnnotation to the map.
            pointAnnotationManager?.create(pointAnnotationOptions)
            pointAnnotationManager?.addClickListener {
                mapView.getMapboxMap().setCamera(CameraOptions.Builder().zoom(14.8).anchor(mapView.getMapboxMap().pixelForCoordinate(
                    Point.fromLngLat(long, lat))).build())
                true
            }
        }
    }
    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
            // copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()
            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.location
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView.gestures.removeOnMoveListener(onMoveListener)
    }
}

data class DestinationsUiModel(
    val address: String = "",
    @DrawableRes val image: Int
)