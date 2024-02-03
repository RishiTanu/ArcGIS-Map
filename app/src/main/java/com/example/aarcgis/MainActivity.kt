package com.example.aarcgis

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.esri.arcgisruntime.geoanalysis.LocationViewshed
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.layers.ArcGISSceneLayer
import com.esri.arcgisruntime.mapping.ArcGISScene
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.AnalysisOverlay
import com.esri.arcgisruntime.mapping.view.Camera
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.SceneView


class MainActivity : AppCompatActivity() {
    private lateinit var sceneView: SceneView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup the SceneView
        sceneView = findViewById(R.id.sceneView)
        setupScene()
    }

    private fun setupScene() {
        // Create a scene and set it to the scene view
        sceneView.scene = ArcGISScene().apply {
            // Set the basemap with imagery
            basemap = Basemap.createImagery()

            // Add elevation data
            baseSurface.elevationSources.add(ArcGISTiledElevationSource("https://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"))

            // Add building data
            operationalLayers.add(ArcGISSceneLayer("https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/Building_Berlin/SceneServer"))
        }

        // Create a camera and set it as the viewpoint for when the scene loads
        val camera = Camera(Point(13.377704, 52.516275, 450.0), 300.0, 75.0, 0.0)
        sceneView.setViewpointCamera(camera)
    }

    private fun addGraphicsOverlay() {
        // Adding a graphics overlay for dynamic content
        val graphicsOverlay = GraphicsOverlay().apply {
           // renderingMode = RenderingMode.DYNAMIC
        }
        sceneView.graphicsOverlays.add(graphicsOverlay)
    }

    private fun addViewshedAnalysis() {
        // Adding an analysis like Viewshed
        val observerPoint = Point(13.377704, 52.516275, 450.0) // Long, Lat, Altitude
        val heading = 45.0
        val pitch = 100.0
        val horizontalAngle = 90.0
        val verticalAngle = 20.0 // Adjust as needed
        val minDistance = 0.0
        val maxDistance = 1500.0 // Adjust as needed

        val viewshed = LocationViewshed(observerPoint, heading, pitch, horizontalAngle, verticalAngle, minDistance, maxDistance)
        val analysisOverlay = AnalysisOverlay().apply {
            analyses.add(viewshed)
        }
        sceneView.analysisOverlays.add(analysisOverlay)
    }

    private fun controlCamera() {
        // Controlling the camera
        val cameraPosition = Camera(Point(13.377704, 52.516275, 450.0), 300.0, 75.0, 0.0)
        sceneView.setViewpointCameraAsync(cameraPosition, 5f) // 5 seconds animation
    }

    override fun onPause() {
        sceneView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        sceneView.resume()
    }

    override fun onDestroy() {
        sceneView.dispose()
        super.onDestroy()
    }
}