package com.example.aarcgis

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.esri.arcgisruntime.geoanalysis.LocationViewshed
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.layers.ArcGISSceneLayer
import com.esri.arcgisruntime.mapping.ArcGISScene
import com.esri.arcgisruntime.mapping.ArcGISTiledElevationSource
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.view.AnalysisOverlay
import com.esri.arcgisruntime.mapping.view.AtmosphereEffect
import com.esri.arcgisruntime.mapping.view.Camera
import com.esri.arcgisruntime.mapping.view.Graphic
import com.esri.arcgisruntime.mapping.view.GraphicsOverlay
import com.esri.arcgisruntime.mapping.view.LightingMode
import com.esri.arcgisruntime.mapping.view.SceneView
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol
import com.esri.arcgisruntime.symbology.SimpleMarkerSymbol.Style
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var sceneView: SceneView
    // Define this as a global property if you need to cancel it when the activity is destroyed
    private var animationJob: Job? = null
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
            baseSurface.elevationSources.add(
                ArcGISTiledElevationSource(
                    "https://elevation3d.arcgis.com/arcgis/rest/services/WorldElevation3D/Terrain3D/ImageServer"
                )
            )

            // Add building data
            operationalLayers.add(
                ArcGISSceneLayer(
                    "https://tiles.arcgis.com/tiles/P3ePLMYs2RVChkJx/arcgis/rest/services/Building_Berlin/SceneServer"
                )
            )

            // Apply sun lighting with shadows to the base surface
            sceneView.sunLighting = LightingMode.LIGHT_AND_SHADOWS

            // Apply realistic atmosphere effect to the scene view
            sceneView.atmosphereEffect = AtmosphereEffect.REALISTIC

            // Create a camera and set it as the viewpoint for when the scene loads
            val initialCameraPosition = Point(13.377704, 52.516275, 1450.0, SpatialReferences.getWgs84())
            val camera = Camera(initialCameraPosition, 300.0, 75.0, 0.0)
            sceneView.setViewpointCamera(camera)

            // Add marker after the scene is set up
            addMarkerAtCameraPosition(initialCameraPosition)
        }

        /*// Create a camera and set it as the viewpoint for when the scene loads
        val camera = Camera(Point(13.377704, 52.516275, 450.0), 300.0, 75.0, 0.0)

        sceneView.setViewpointCamera(camera)
        addMarkerAtCameraPosition(initialCameraPosition)*/
        addViewshedAnalysis()
    }
    private fun addMarkerAtCameraPosition(initialPosition: Point) {
        // Adjust the altitude to 1000
        val startPosition = Point(initialPosition.x, initialPosition.y, 10000.0, initialPosition.spatialReference)

        // Create a simple marker symbol
        val markerSymbol = SimpleMarkerSymbol(Style.CIRCLE, 0xFF0000FF.toInt(), 20f) // Blue circle

        // Create a graphic with the point and symbol
        val markerGraphic = Graphic(startPosition, markerSymbol)

        // Create a graphics overlay and add the graphic to it
        val graphicsOverlay = GraphicsOverlay()
        graphicsOverlay.graphics.add(markerGraphic)

        // Add the graphics overlay to the scene view
        sceneView.graphicsOverlays.add(graphicsOverlay)

        // Animate the marker from left to right
        val moveDistance = 0.0001 // Adjust this value for the distance of each step
        animationJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                val currentPoint = markerGraphic.geometry as Point
                val newPoint = Point(currentPoint.x + moveDistance, currentPoint.y, currentPoint.z, currentPoint.spatialReference)
                markerGraphic.geometry = newPoint
                delay(1000) // Adjust this value for the interval of movement
            }
        }
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
        animationJob?.cancel() // Cancel the animation job when the activity is destroyed
        super.onDestroy()
    }
}