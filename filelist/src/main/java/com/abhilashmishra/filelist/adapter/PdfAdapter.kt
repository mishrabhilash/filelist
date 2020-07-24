package com.abhilashmishra.filelist.adapter

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.abhilashmishra.filelist.R
import com.abhilashmishra.filelist.view_holder.PdfItemViewHolder
import com.bumptech.glide.RequestManager

class PdfAdapter(private val pageWidth: Int, private val glide: RequestManager) :
    RecyclerView.Adapter<PdfItemViewHolder>() {

    private var renderer: PdfRenderer? = null

    override fun getItemCount(): Int {
        return renderer?.pageCount ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfItemViewHolder {
        return PdfItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.pdf_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: PdfItemViewHolder, position: Int) {
        try {
            glide
                .load(renderer!!.openPage(position).renderAndClose(pageWidth))
                .into(holder.imageView)
        } catch (e: KotlinNullPointerException) {
            e.printStackTrace()
        }
    }

    fun setRenderer(pdfRenderer: PdfRenderer) {
        renderer = pdfRenderer
        notifyDataSetChanged()
    }

    private fun PdfRenderer.Page.renderAndClose(width: Int): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = createBitmap(width)
            render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close()
        }
        return bitmap
    }

    private fun PdfRenderer.Page.createBitmap(bitmapWidth: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(
            bitmapWidth, (bitmapWidth.toFloat() / width * height).toInt(), Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(bitmap, 0f, 0f, null)

        return bitmap
    }

}