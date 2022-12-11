package com.example.location.presentation

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import com.example.location.LocationActivity
import com.example.location.databinding.FragmentBottomSheetBinding
import com.example.location.domain.TerminalsDomainModel

class BottomSheetFragment: Fragment() {
    private var _binding: FragmentBottomSheetBinding? = null
    private val binding: FragmentBottomSheetBinding
        get() {
            return _binding!!
        }
    private lateinit var anim: Animator
    private lateinit var myView: LinearLayout
    private lateinit var terminalsModel:TerminalsDomainModel

    companion object {
        fun newInstance(model: TerminalsDomainModel): BottomSheetFragment {
            val args = Bundle().apply {
                putParcelable("model", model)
            }
            val fragment = BottomSheetFragment().apply {
                arguments = args
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        arguments?.let {

            terminalsModel = it.getParcelable("model")!!
            Log.d(this.javaClass.simpleName, "From model: ${it}")
        }

        _binding = FragmentBottomSheetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val progressBar: ProgressBar = binding.progressBar
        val objectAnimator: ObjectAnimator = ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, 100).setDuration(if (this::terminalsModel.isInitialized) terminalsModel.expireDuration.toLong() else 5000)
        objectAnimator.addUpdateListener {
            val progress = it.animatedValue as Int
            Log.d(this.javaClass.simpleName, "From model: ${it.animatedValue}")

            progressBar.progress = progress
            if (activity != null)
            if (progress == 100) (activity as LocationActivity).finish()
        }
        objectAnimator.start()
        val btn = binding.textViewButton

        btn.setOnLongClickListener {
            myView = binding.revealiew
            val cx = myView.width/2
            val cy = myView.height/2
            val finalRadius = Math.max(myView.width, myView.height)
            anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, finalRadius.toFloat())
            anim.duration = 3000
            myView.visibility = View.VISIBLE

            anim.addListener(object: Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {
                    Log.d("animation", "onAnimationStart")
                    //TODO("Not yet implemented")
                }

                override fun onAnimationEnd(animation: Animator?) {
                    (activity as LocationActivity).finish()


                    Log.d("animation", "onAnimationEnd: ${animation?.isRunning}")
                }

                override fun onAnimationCancel(animation: Animator?) {

                    Log.d("animation", "onAnimationCancel")
                }

                override fun onAnimationRepeat(animation: Animator?) {

                    Log.d("animation", "onAnimationRepeat")
                }
            })
            anim.start()
            true
        }

        btn.setOnTouchListener { v, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                }
                MotionEvent.ACTION_UP -> {
                    myView.visibility = View.GONE
                    anim.removeAllListeners()

                }
                else -> {}
            }

            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}