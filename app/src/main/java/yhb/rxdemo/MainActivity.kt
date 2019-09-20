package yhb.rxdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

const val tag = "rxDemo"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val disposable =
            Observable.create<Int> { emitter ->
                emitter.onNext(3)
                emitter.onNext(6)
                emitter.onNext(9)
                Log.i(tag, "[emit finished.]:" + Thread.currentThread().name)
            }
                .map {
                    Log.i(tag, "map-1:" + Thread.currentThread().name)
                    it
                }
                .map {
                    Log.i(tag, "map-2:" + Thread.currentThread().name)
                    it
                }
                .observeOn(Schedulers.io())
                .map {
                    Log.i(tag, "map-3:" + Thread.currentThread().name)
                    it
                }
                .flatMap {
                    Observable.just(it - 1, it - 2, it - 3)
                }
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    Log.i(tag, "doOnSubscribe: $it, " + Thread.currentThread().name)
                }.doOnNext {
                    Log.i(tag, "[doOnNext] , " + Thread.currentThread().name)
                }.doAfterNext {
                    Log.i(tag, "[doAfterNext] , " + Thread.currentThread().name)
                }
                .subscribe(object : Observer<Int> {
                    override fun onComplete() {
                        Log.i(tag, "[onComplete] " + Thread.currentThread().name)
                    }

                    override fun onNext(it: Int) {
                        Log.i(tag, "[onNext] $it, " + Thread.currentThread().name)
                    }

                    override fun onSubscribe(d: Disposable) {
                        Log.i(tag, "[onSubscribe] $d, " + Thread.currentThread().name)
                    }

                    override fun onError(e: Throwable) {
                        Log.i(tag, "[onError] $e, " + Thread.currentThread().name)
                    }
                })

    }
}
