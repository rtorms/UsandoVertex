package br.utfpr.usandovertex

import android.content.Intent
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import br.utfpr.usandovertex.databinding.ActivityAnalisarBinding
import com.google.api.gax.core.FixedCredentialsProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.vision.v1.AnnotateImageRequest
import com.google.cloud.vision.v1.AnnotateImageResponse
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse
import com.google.cloud.vision.v1.DominantColorsAnnotation
import com.google.cloud.vision.v1.Feature
import com.google.cloud.vision.v1.Feature.Type
import com.google.cloud.vision.v1.Image
import com.google.cloud.vision.v1.ImageAnnotatorClient
import com.google.cloud.vision.v1.ImageAnnotatorSettings
import com.google.cloud.vision.v1.ImageContext
import com.google.cloud.vision.v1.ImageSource
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream


class AnalisarActivity : AppCompatActivity() {
    private lateinit var tvDescricao: TextView
    private lateinit var binding: ActivityAnalisarBinding
    private lateinit var url: String
    private lateinit var progressBar: ProgressBar

    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalisarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        tvDescricao = binding.tvAnalise
        val i: Intent = intent
        url = i.getStringExtra("url").toString()
        detectPropertiesGcs()
    }

    private fun getVisionService(): ImageAnnotatorClient {
        val inputStream: InputStream = assets.open("credencial.json")
        val json: String = inputStream.bufferedReader().use { it.readText() }

        val credentials = GoogleCredentials.fromStream(ByteArrayInputStream(json.toByteArray()))
        return ImageAnnotatorClient.create(
            ImageAnnotatorSettings.newBuilder()
                .setCredentialsProvider(FixedCredentialsProvider.create(credentials))
                .build()
        )
    }

    @Throws(IOException::class)
    fun detectPropertiesGcs() {
        val filePath = url
//        detectPropertiesGcs(filePath)
//        detectLocalizedObjectsGcs(filePath)
        detectTextGcs(filePath)
    }


    @Throws(IOException::class)
    fun detectTextGcs(gcsPath: String?) {
        val requests: MutableList<AnnotateImageRequest> = ArrayList()
        val imgSource = ImageSource.newBuilder().setImageUri(gcsPath).build()
        val img = Image.newBuilder().setSource(imgSource).build()
        val feat = Feature.newBuilder().setType(Type.TEXT_DETECTION).build()
        val imageContext = ImageContext.newBuilder().addLanguageHints("pt").build()
        val request = AnnotateImageRequest.newBuilder().addFeatures(feat)
            .setImage(img).setImageContext(imageContext).build()
        requests.add(request)
        val saida = StringBuilder()
        val vision = getVisionService()
        vision.use { client ->
            val response: BatchAnnotateImagesResponse = client.batchAnnotateImages(requests)
            val responses: List<AnnotateImageResponse> = response.responsesList
            for (res in responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.error.message)
                    return
                }
                for (annotation in res.textAnnotationsList) {
                    val json = JSONObject()
                    json.put("Descrição", annotation.description)
                    saida.append(json.toString())
                    saida.append("\n")
                }
            }
        }
        tvDescricao.text = saida
    }

    @Throws(IOException::class)
    fun detectPropertiesGcs(gcsPath: String?) {
        val requests: MutableList<AnnotateImageRequest> = ArrayList()
        val imgSource: ImageSource = ImageSource.newBuilder().setImageUri(gcsPath).build()
        val img: Image = Image.newBuilder().setSource(imgSource).build()
        val feat = Feature.newBuilder().setType(Feature.Type.IMAGE_PROPERTIES).build()
        val request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build()
        requests.add(request)
        val vision = getVisionService()
        vision.use { client ->
            val response =
                client.batchAnnotateImages(requests)
            val responses =
                response.responsesList
            for (res in responses) {
                if (res.hasError()) {
                    System.out.format("Error: %s%n", res.error.message)
                    return
                }
                val colors: DominantColorsAnnotation =
                    res.imagePropertiesAnnotation.dominantColors
                for (color in colors.colorsList) {
                    System.out.format(
                        "fraction: %f%nr: %f, g: %f, b: %f%n",
                        color.pixelFraction,
                        color.color.red,
                        color.color.green,
                        color.color.blue
                    )
                }
            }
        }
    }

    @Throws(IOException::class)
    fun detectLocalizedObjectsGcs(gcsPath: String?) {
        val requests: MutableList<AnnotateImageRequest> = ArrayList()
        val imgSource = ImageSource.newBuilder().setImageUri(gcsPath).build()
        val img = Image.newBuilder().setSource(imgSource).build()
        val request = AnnotateImageRequest.newBuilder()
            .addFeatures(Feature.newBuilder().setType(Type.OBJECT_LOCALIZATION))
            .setImage(img).build()
        requests.add(request)
        val vision = getVisionService()
        vision.use { client ->
            val response =
                client.batchAnnotateImages(requests)
            val responses = response.responsesList
            client.close()
            val saida = StringBuilder()
            for (res in responses) {
                for (entity in res.localizedObjectAnnotationsList) {
                    saida.append(entity.name)
                    saida.append("\n")
                    entity.boundingPoly.normalizedVerticesList
                        .forEach { vertex ->
                            System.out.format("- (%s, %s)%n", vertex.x, vertex.y)
                        }
                }
            }
            tvDescricao.text = saida
        }
    }

}