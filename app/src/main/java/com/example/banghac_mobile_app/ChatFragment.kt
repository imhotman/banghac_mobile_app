package com.example.banghac_mobile_app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class ChatFragment : Fragment(), OnMapReadyCallback {
    private lateinit var googleMap: GoogleMap
    private var locationManager: LocationManager? = null
    private var locationListener: LocationListener? = null
    private var previousMarker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_chat, container, false)

        // SupportMapFragment 가져오기
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // EditText와 Button 초기화
        val searchLocation = rootView.findViewById<EditText>(R.id.search_location)
        val searchButton = rootView.findViewById<Button>(R.id.search_button)

        searchButton.setOnClickListener {
            val address = searchLocation.text.toString()
            val latLng = getLatLngFromAddress(requireContext(), address)
            if (latLng != null) {
                googleMap.clear() // 기존 마커 제거
                googleMap.addMarker(MarkerOptions().position(latLng).title(address))
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }
        // 위치 관리자 및 리스너 설정
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // 위치 변경시 호출되며, 새 위치로 지도 업데이트
                val currentLocation = LatLng(location.latitude, location.longitude)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                googleMap.addMarker(MarkerOptions().position(currentLocation).title("현재 위치"))
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))
                googleMap.moveCamera(CameraUpdateFactory.zoomTo(15f))
            }
        }

        // 위치 권한 확인 및 위치 업데이트 요청
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 위치 권한 요청 처리
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // 위치 업데이트 요청
            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener
            )
        }

        return rootView
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // 지도 초기화 및 설정
        googleMap.setOnMapClickListener { latLng ->
            // 이전에 클릭한 마커 제거
            previousMarker?.remove()

            // 클릭한 위치에 마커 추가
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title("클릭한 위치")
            )
            // 마커 클릭 시 InfoWindow 표시
            marker?.showInfoWindow()

            // 이전에 클릭한 마커 업데이트
            previousMarker = marker

            val latitude = latLng.latitude
            val longitude = latLng.longitude
            val message = "클릭한 위치의 좌표: $latitude, $longitude"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onStop() {
        super.onStop()
        // 프래그먼트가 중단될 때 위치 업데이트 리스너 해제
        locationManager?.removeUpdates(locationListener!!)
    }
    fun getLatLngFromAddress(context: Context, address: String): LatLng? {
        val geocoder = Geocoder(context)
        val addresses = geocoder.getFromLocationName(address, 1)
        if (addresses!!.isNotEmpty()) {
            val lat = addresses[0].latitude
            val lng = addresses[0].longitude
            return LatLng(lat, lng)
        }
        return null
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 123
        private const val MIN_TIME_BETWEEN_UPDATES = 1000L // 1초
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES = 10f // 10미터
    }

}
