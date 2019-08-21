package com.example.babajidemustapha.survey.features.introduction


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import com.example.babajidemustapha.survey.R
import kotlinx.android.synthetic.main.fragment_introduction.*
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

/**
 * A simple [Fragment] subclass.
 * Use the [IntroductionFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class IntroductionFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var featureHeaderText: String by Delegates.notNull()
    private var featureDetailText: String by Delegates.notNull()
    private var imageId: Int by Delegates.notNull()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        feature_image.setImageResource(imageId)
        feature_text_header.text = featureHeaderText
        feature_text_detail.text = featureDetailText
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            featureHeaderText = it.getString(ARG_PARAM1, "")
            featureDetailText = it.getString(ARG_PARAM2, "")
            imageId = it.getInt(ARG_PARAM3)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_introduction, container, false)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment IntroductionFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(featureHeader: String, featureDetail: String, @DrawableRes imageId: Int) =
                IntroductionFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, featureHeader)
                        putString(ARG_PARAM2, featureDetail)
                        putInt(ARG_PARAM3, imageId)
                    }
                }
    }
}
