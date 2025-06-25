package fastcampus.aos.part2.part2_chapter8

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.SearchView.OnQueryTextListener
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.Tm128
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import fastcampus.aos.part2.part2_chapter8.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Query

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMainBinding
    private lateinit var naverMap: NaverMap
    private var isMapInit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mapView.onCreate(savedInstanceState)

        binding.mapView.getMapAsync(this)

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query?.isNotEmpty() == true) {
                    SearchRepository.getGoodRestaurant(query).enqueue(object : Callback<SearchResult> {
                            override fun onResponse(
                                call: Call<SearchResult?>,
                                response: Response<SearchResult?>
                            ) {
                                Log.e("hyunsu", "Response: ${response.body()?.items}")

                                val searchItemList = response.body()?.items.orEmpty()

                                if (searchItemList.isEmpty()) {
                                    Toast.makeText(this@MainActivity, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                                    return
                                } else if (isMapInit.not()) {
                                    Toast.makeText(this@MainActivity, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                                    return
                                }

                                val markers = searchItemList.map {
                                    val lat = it.mapy.toDouble() / 1_000_0000
                                    val lng = it.mapx.toDouble() / 1_000_0000
                                    val position = LatLng(lat, lng)

                                    Marker().apply {
                                        this.position = position
                                        captionText = it.title
                                        map = naverMap
                                    }
                                }

                                searchItemList.forEach {
                                    Log.e("hyunsu", "검색 좌표 title: ${it.title}, mapx: ${it.mapx}, mapy: ${it.mapy}")
                                }

                                val cameraUpdate = CameraUpdate.scrollTo(markers.first().position)
                                    .animate(CameraAnimation.Easing)
                                naverMap.moveCamera(cameraUpdate)


                            }

                            override fun onFailure(
                                call: Call<SearchResult?>,
                                t: Throwable
                            ) {
                                Log.e("hyunsu", "Throwable: $t")
                            }
                        })

                    return false
                } else {
                    return true
                }
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onMapReady(mapObject: NaverMap) {
        naverMap = mapObject
        isMapInit = true
    }


}