package com.example.alex.arduino

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_land.*
import ninja.eigenein.joypad.JoypadView
import java.io.IOException
import java.lang.Math.abs
import java.lang.Math.round

class MainActivity : AppCompatActivity(), SensorEventListener,  JoypadView.Listener {
    override fun onUp() {}
    private var swi : Boolean = true

    private var mSensorManager : SensorManager?= null
    private var mAccelerometer : Sensor ?= null

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(p0: SensorEvent?) {
        if (!swi && sw.isChecked)
            direccion(round(p0!!.values[0]), round(p0!!.values[1]))
    }

    fun direccion(x: Int,y: Int){
        if (x == 0 && y == 0) {
            imageView.setImageResource(R.drawable.stop)
            outputMessagesSubject.onNext(A)
        }else if (abs(x) >=  abs(y)) {
            if (x < 0) {
                imageView.setImageResource(R.drawable.norte)
                outputMessagesSubject.onNext(F)
            }else {
                imageView.setImageResource(R.drawable.sur)
                outputMessagesSubject.onNext(G)
            }
        }else {
            if (y < 0 && x <= 0) {
                imageView.setImageResource(R.drawable.noroeste)
                outputMessagesSubject.onNext(C)
            }else if (y < 0 && x > 0) {
                imageView.setImageResource(R.drawable.suroste)
                outputMessagesSubject.onNext(B)
            }else if (y > 0 && x <= 0) {
                imageView.setImageResource(R.drawable.noreste)
                outputMessagesSubject.onNext(D)
            }else {
                imageView.setImageResource(R.drawable.sureste)
                outputMessagesSubject.onNext(E)
            }
        }
    }


    private val logTag = MainActivity::class.java.simpleName
    private val connectRetriesCount = 5L

    private val outputMessagesSubject = PublishSubject.create<OutputMessage>()
    private val connectionDisposable = CompositeDisposable()


    private lateinit var progressBar: ProgressBar

    private lateinit var deviceNameMenuItem: MenuItem
    private lateinit var vccMenuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
       // setContentView(R.layout.activity_main)
        setContentView(R.layout.activity_main_land)

        joypad_view.setListener(this)
        btn_joystick.setOnClickListener({boton1()})
        btn_sensor.setOnClickListener{(boton2())}
       // progressBar = findViewById(R.id.progress_bar)
    }

    fun boton1() {
        btn_joystick.isEnabled= false
        btn_sensor.isEnabled=true
        swi=true
    }
    fun boton2() {
        btn_joystick.isEnabled= true
        btn_sensor.isEnabled=false
        swi=false
    }



    override fun onMove(distancia: Float, x: Float, y: Float) {
        if (swi && sw.isChecked) {
            textView.setText("p0: $distancia, x: ${round(x)}, y: ${round(y)}"); //To change body of created functions use File | Settings | File Templates.
            println("distancia: $distancia, x: $x, y: $y")//To change body of created functions use File | Settings | File Templates.
            joystcik(round(x), round(y))
        }
    }

    fun joystcik(x: Int,y: Int){
        if (x == 0 && y == 0) {
            imageView.setImageResource(R.drawable.stop)
            outputMessagesSubject.onNext(A)
        }else if (abs(x) <  abs(y)) {
            if (y == 1) {
                imageView.setImageResource(R.drawable.norte)
                outputMessagesSubject.onNext(F)
            }else {
                imageView.setImageResource(R.drawable.sur)
                outputMessagesSubject.onNext(G)
            }
        }else {
            if (y == 1 && x == -1) {
                imageView.setImageResource(R.drawable.noroeste)
                outputMessagesSubject.onNext(C)
            }else if (y == -1 && x == -1) {
                imageView.setImageResource(R.drawable.suroste)
                outputMessagesSubject.onNext(B)
            }else if (y == 1 && x == 1) {
                imageView.setImageResource(R.drawable.noreste)
                outputMessagesSubject.onNext(D)
            }else {
                imageView.setImageResource(R.drawable.sureste)
                outputMessagesSubject.onNext(E)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        deviceNameMenuItem = menu.findItem(R.id.menu_main_device_name)
        vccMenuItem = menu.findItem(R.id.menu_main_vcc)
        return true
    }

    override fun onResume() {
        super.onResume()
        mSensorManager!!.registerListener(this,mAccelerometer,
                SensorManager.SENSOR_DELAY_NORMAL)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_main_connect -> {
                connectionDisposable.clear() // close any existing connection beforehand
                showDevicesDialog { connectTo(it) }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onPause() {
        super.onPause()
        connectionDisposable.clear()
        mSensorManager!!.unregisterListener(this)
    }


    private fun showDevicesDialog(listener: (BluetoothDevice) -> Unit) {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (!bluetoothAdapter.isEnabled) {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 0)
            return
        }
        val devices = bluetoothAdapter.bondedDevices.sortedBy { it.name }.toTypedArray()
        if (devices.isEmpty()) {
            Toast.makeText(this, R.string.toast_no_paired_devices, Toast.LENGTH_LONG).show()
            return
        }
        showAlertDialog(this) {
            setTitle(R.string.dialog_title_choose_vehicle)
            setCancelable(true)
            setItems(devices.map { it.name }.toTypedArray(), { _, which -> listener(devices[which]) })
        }
    }

    private fun connectTo(device: BluetoothDevice) {
        connectionDisposable.clear() // close any still existing connection
        connectionDisposable.add(
                device.messages(outputMessagesSubject)
                        .subscribeOn(Schedulers.newThread())
                        .retry(connectRetriesCount)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                { onInputMessage(it) },
                                {
                                    if (it !is IOException) {
                                        throw RuntimeException(it) // non-IO errors shouldn't be suppressed
                                    }
                                    Log.e(logTag, "Connection to " + device.name + " failed")
                                    sw.isChecked=false
                                    Toast.makeText(this, getString(R.string.toast_connection_failed, device.name), Toast.LENGTH_SHORT).show()
                                   // progressBar.gone()
                                },
                                { Log.e(logTag, "Connection stream ended") },
                                {
                                    // FIXME: the handler doesn't work on retries.
                                    Toast.makeText(this, getString(R.string.toast_connecting, device.name), Toast.LENGTH_SHORT).show()
                                    //progressBar.show()
                                }
                        )
        )
    }

    private fun onInputMessage(message: InputMessage) {
        Log.d(logTag, "Input message: %s".format(message))
        when (message) {
            is ConnectedMessage -> {
                deviceNameMenuItem.title = message.device_name
                Toast.makeText(this, getString(R.string.toast_connected, message.device_name), Toast.LENGTH_SHORT).show()
                sw.isChecked = true
               // progressBar.gone()
            }
            is DeprecatedTelemetryMessage -> vccMenuItem.title = "%.2fV".format(message.vcc)
        }
    }
}
