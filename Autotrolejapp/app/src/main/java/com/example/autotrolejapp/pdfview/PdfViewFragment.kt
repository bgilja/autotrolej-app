package com.example.autotrolejapp.pdfview


import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.autotrolejapp.R
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.tabs.TabLayout
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class PdfViewFragment : Fragment() {
    var pdfView: PDFView? = null
    var loadingIndicator: CircularProgressIndicator? = null
    private var lineId: String? = null

    companion object {
        fun newInstance(lineId: String?): PdfViewFragment? {
            val bundle = Bundle()
            bundle.putString("lineId", lineId)
            val fragment = PdfViewFragment()
            fragment.arguments = bundle
            return fragment
        }

        val mutableMapOfLineSchedules: HashMap<String, String> = mutableMapOf(
            //LOCAL
            "KBC" to "https://www.autotrolej.hr/att/wp-content/uploads/2020/04/Lin-KBC-va%C5%BEi-od-27.04.2020.pdf",
            "1" to "https://www.autotrolej.hr/att/wp-content/uploads/2021/03/lin-1-vazi-od-17032021.pdf",
            "1A"  to "https://www.autotrolej.hr/att/wp-content/uploads/2020/09/lin-1a-vazi-od-07-09-2020.pdf",
            "1B"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/02/lin-1b-vazi-od-15022021.pdf",
            "2"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/05/lin-2-vazi-od-24052021.pdf",
            "2A"  to "https://www.autotrolej.hr/att/wp-content/uploads/2016/04/lin-2a-vazi-od-15022021.pdf",
            "3"  to "https://www.autotrolej.hr/att/wp-content/uploads/2016/04/lin-3-vazi-od-15022021.pdf",
            "3A"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/05/lin-3a-vazi-od-10052021.pdf",
            "4"  to "https://www.autotrolej.hr/att/wp-content/uploads/2016/04/lin-4-vazi-od-15022021.pdf",
            "4A"  to "https://www.autotrolej.hr/att/wp-content/uploads/2016/04/lin-4a-vazi-od-15022021.pdf",
            "5"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/05/lin-5-vazi-od-24052021.pdf",
            "5A"  to "https://www.autotrolej.hr/att/wp-content/uploads/2016/05/5a-26042021.pdf",
            "6"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/05/lin-6-vazi-od-24052021.pdf",
            "7"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/05/lin-7-vazi-od-24052021.pdf",
            "7A"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/05/lin-7a-vazi-od-24052021.pdf",
            "8"  to "https://www.autotrolej.hr/att/wp-content/uploads/2016/04/lin-8-vazi-od-15022021.pdf",
            "13"  to "https://www.autotrolej.hr/att/wp-content/uploads/2020/12/lin-13-vazi-od-24122020.pdf",
            //WIDE
            "10"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l10-10a-27-29-29a-02042021.pdf",
            "10A"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l10-10a-27-29-29a-02042021.pdf",
            "27"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l10-10a-27-29-29a-02042021.pdf",
            "29"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l10-10a-27-29-29a-02042021.pdf",
            "11"  to "https://www.autotrolej.hr/att/wp-content/uploads/2016/05/l11-02042021is.pdf",
            "12"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/06/12-14-16-15-05062021.pdf",
            "12A"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/06/12-14-16-15-05062021.pdf",
            "12B"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/06/12-14-16-15-05062021.pdf",
            "14"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/06/12-14-16-15-05062021.pdf",
            "15"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/06/12-14-16-15-05062021.pdf",
            "15A"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/06/12-14-16-15-05062021.pdf",
            "16"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/06/12-14-16-15-05062021.pdf",
            "16A"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/06/12-14-16-15-05062021.pdf",
            "18"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l18-18b-02042021.pdf",
            "18B"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l18-18b-02042021.pdf",
            "19"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l19-20-21-02042021.pdf",
            "20"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l19-20-21-02042021.pdf",
            "21"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l19-20-21-02042021.pdf",
            "22"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l22-23-30-02042021.pdf",
            "23"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l22-23-30-02042021.pdf",
            "30"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l22-23-30-02042021.pdf",
            "25"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l25-26-02042021.pdf",
            "26"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l25-26-02042021.pdf",
            "32"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l32-32a-02042021.pdf",
            "32A"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l32-32a-02042021.pdf",
            "34"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l34-37-02042021.pdf",
            "37"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/04/l34-37-02042021.pdf",
            "35"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/01/l35-01022021.pdf",
            "36"  to "https://www.autotrolej.hr/att/wp-content/uploads/2021/01/l36-01022021.pdf",
            //NIGHT
            "102"  to "https://www.autotrolej.hr/att/wp-content/uploads/2016/04/Lin-102-va%C5%BEi-od-12.01.2020.pdf",
            "103"  to "https://www.autotrolej.hr/att/wp-content/uploads/2016/04/lin-103-05-09-2016.pdf",
        ) as HashMap<String, String>

    }

    private fun readBundle(bundle: Bundle?) {
        if (bundle != null) {
            lineId = bundle.getString("lineId")
        }
    }

    private lateinit var viewModel: PdfViewViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        readBundle(arguments);

        return inflater.inflate(R.layout.pdf_view_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val noAvailableSchedule = view.findViewById<View>(R.id.no_lines_schedule)
        loadingIndicator = view.findViewById<CircularProgressIndicator>(R.id.loading_schedule)
        pdfView = view.findViewById(R.id.pdfView)

        //TODO: nadi raspored od linije
        if(lineId != null) {
            val lineScheduleURL = mutableMapOfLineSchedules[lineId]
            if (lineScheduleURL != null) {
                loadingIndicator?.visibility = View.VISIBLE
                RetrivePDFfromUrl().execute(lineScheduleURL)
            } else {
                loadingIndicator?.visibility = View.GONE
                noAvailableSchedule.visibility = View.VISIBLE
            }
        } else {
            noAvailableSchedule.visibility = View.VISIBLE
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PdfViewViewModel::class.java)
        // TODO: Use the ViewModel
    }

    // create an async task class for loading pdf file from URL.
    inner class RetrivePDFfromUrl :
        AsyncTask<String?, Void?, InputStream?>() {
        override fun doInBackground(vararg params: String?): InputStream? {
            var inputStream: InputStream? = null
            try {
                val url = URL(params[0])
                val urlConnection: HttpURLConnection = url.openConnection() as HttpsURLConnection
                if (urlConnection.responseCode == 200) {
                    inputStream = BufferedInputStream(urlConnection.inputStream)
                }
            } catch (e: IOException) {
                e.printStackTrace()
                loadingIndicator?.visibility = View.GONE
                return null
            }
            return inputStream
        }

        override fun onPostExecute(inputStream: InputStream?) {
            // after the execution of our async
            loadingIndicator?.visibility = View.GONE
            Log.d("Iz onPostExecute", inputStream.toString())
            pdfView?.fromStream(inputStream)?.load()

        }
    }

}