package com.example.lifesum.food

import android.content.Context.SENSOR_SERVICE
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.base.platform.BaseFragment
import com.example.base.utils.Loading
import com.example.base.utils.Result
import com.example.base.utils.hide
import com.example.base.utils.show
import com.example.base.viewbinding.viewBinding
import com.example.lifesum.R
import com.example.lifesum.common.food.model.entites.view.FoodView
import com.example.lifesum.databinding.FragmentFoodDetailBinding
import com.squareup.seismic.ShakeDetector
import org.koin.android.ext.android.getKoin
import org.koin.androidx.viewmodel.koin.getStateViewModel


class FoodDetailFragment : BaseFragment<FoodDetailViewModel>(), ShakeDetector.Listener {
    private val binding by viewBinding(FragmentFoodDetailBinding::bind)
    override val viewModel by lazy {
        getKoin().getStateViewModel(requireActivity(), FoodDetailViewModel::class)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sensorManager = requireContext().getSystemService(SENSOR_SERVICE) as SensorManager?
        val sd = ShakeDetector(this)
        sd.start(sensorManager, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getSavedData().observe(viewLifecycleOwner) { data ->
            data?.let { showData(it) }
        }
    }

    private fun showData(data: FoodView) {
        showView()
        with(binding) {
            tvFoodName.text = data.title
            tvValue.text = data.calories.toString()
            tvCarbsValue.text = data.carbs.toString()
            tvProteinValue.text = data.protein.toString()
            tvFatValue.text = data.fat.toString()
        }
    }

    private fun hideViews() {
        binding.extraDetailsContainer.hide()
        binding.circleContainer.hide()
    }

    private fun showView() {
        binding.extraDetailsContainer.show()
        binding.circleContainer.show()
    }

    override fun showLoading(loading: Loading) {
        //TODO add loading skeleton and hide views
    }

    override fun hideLoading(loading: Loading) {
        //TODO hide loading skeleton
    }

    override fun showError(error: Result.Error) {
        hideViews()
        super.showError(error)
    }

    override fun hearShake() {
        viewModel.getFetchState()?.let { state ->
            if (!state)
                viewModel.getData()
        } ?: run {
            viewModel.getData()
        }
    }
}