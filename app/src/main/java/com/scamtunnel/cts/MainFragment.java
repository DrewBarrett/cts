package com.scamtunnel.cts;

import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.VideoView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static TextView textbox;
    BluetoothDevice mmDevice;
    View rootView;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ConnectThread mConnectThread = null;
    ConnectedThread mConnectedThread = null;
    private OnFragmentInteractionListener mListener;

    float yaw = 0;
    float yaw0 = 0;
    float pitch = 0;
    float pitch0 = 0;
    float roll = 0;
    float roll0 = 0;
    float bend = 0;
    float bend0 = 0;

    int bent = 0;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void updateText(String newText) {
        //TextView t = (TextView) this.getView().findViewById(R.id.textViewBlue);
        //t.setText(newText);
        //TextView textView = (TextView) rootView.findViewById(R.id.textViewBlue);
        textbox = (TextView) rootView.findViewById(R.id.textViewBlue);
        textbox.setText(newText);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);

        }

        //textbox = (TextView) rootView.findViewById(R.id.textViewBlue);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        //return inflater.inflate(R.layout.fragment_main, container, false);
        textbox = (TextView) view.findViewById(R.id.textViewBlue);
        //VideoView view1 = (VideoView) view.findViewById(R.id.vView1);
        textbox.setText("Initializing Bluetooth");
        rootView = view;

        final Button button = (Button) view.findViewById(R.id.btnZero);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                yaw0 = yaw;
                pitch0 = pitch;
                roll0 = roll;
                bend0 = bend;
            }
        });
        return view;

        //TextView textView = (TextView) rootView.findViewById(R.id.textViewBlue);
    }

    @Override
    public void onResume() {
        super.onResume();
        startBluetooth();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    public void zero(View view) {

    }

    public void startBluetooth() {
        if (mBluetoothAdapter != null) {
            updateText("Bluetooth Adapter Found");
            //MainFragment fragment = (MainFragment) getFragmentManager().findFragmentById(R.id.fragment_container);
            //fragment_obj.updateText("bluetooth connecting...");
            //firstFragment.updateText("test");
            //fragment_obj.textbox.setText("test");
            //firstFragment.textbox.setText("bluetooth connecting...");
            // ((MainFragment)firstFragment).textbox.setText("bluetooth connecting...");
            //MainFragment placeholderFragment = (MainFragment) getSupportFragmentManager().findFragmentByTag("bluetoothTag");
            boolean started = false;
            while (!mBluetoothAdapter.isEnabled()) {
                if(!started) {
                    updateText("Bluetooth adapter turned off... Turning on...");
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                    started = true;
                }
            }
            //Set pairedDevices = mBluetoothAdapter.getBondedDevices();
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    updateText("Scanning paired devices");
                    Log.d("StartBluetooth", "scanning paried devices");
                    updateText(device.getName());
                    if (device.getName().equals("HC-06")) //Note, you will need to change this to match the name of your device
                    {
                        mmDevice = device;
                        updateText("Bluetooth Device found");
                        Log.d("StartBluetooth", "device found");
                        //firstFragment.updateText("test");
                        break;
                    }
                }
            }
            mConnectThread = new ConnectThread(mmDevice);
            mConnectThread.start();
            updateText("Thread Started");
            Log.d("StartBluetooth", " connectThread start called");

        } else {
            updateText("No Bluetooth Adapter Found");
            Log.d("StartBluetooth", "no bluetooth adapter found.");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
            //crazy talk
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        public ConnectThread(BluetoothDevice device) {
            Log.d("Connect Thread", "thread started");
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.d("Connect Thread", "ConnectThread Failed");
            }
            mmSocket = tmp;
        }

        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            try {
                Log.d("Socket", "Socket Connecting...");
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                    Log.d("Socket", "Socket connecting failed...");
                } catch (IOException closeException) {
                }
                return;
            }
            Log.d("Socket", "Socket connected");
            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();
            Log.d("connected thread","Starting connected thread");
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int) msg.arg1;
            int end = (int) msg.arg2;

            switch (msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    updateText(writeMessage);
                    Log.d("Handler", "message handled");
                    break;
            }
        }
    };
    final Handler testHandler = new Handler();

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //String yaw = "";
        //String pitch = "";
        //String roll = "";
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            //updateText("Connected thread started... Getting streams..");
            Log.d("Connected Thread", "Connected thread started... Getting streams..");
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.d("Connected Thread", "failed to get streams");
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            int readBufferPosition = 0;
            byte[] readBuffer = new byte[1024];

            while (true) {
                try {
                    int bytesAvailable = mmInStream.available();
                    if (bytesAvailable > 0) {
                        byte[] packetbytes = new byte[bytesAvailable];
                        mmInStream.read(packetbytes);
                        for (int i = 0; i < bytesAvailable; i++) {
                            byte b = packetbytes[i];
                            if (b == 10) {
                                byte[] encodedBytes = new byte[readBufferPosition];
                                System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                final String data = new String(encodedBytes, "US-ASCII");
                                readBufferPosition = 0;
                                testHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (data.contains("*") && data.length() > 0 && data.indexOf("$") > data.indexOf("*")) {
                                            //yaw = "yaw: " + data.substring(data.indexOf("*", 0) + 1, data.indexOf("$")) + "\n";
                                            //pitch = "pitch: " + data.substring(data.indexOf("$", 0) + 1, data.indexOf("@")) + "\n";
                                            //roll = "roll: " + data.substring(data.indexOf("@", 0) + 1, data.indexOf("#")) + "\n";
                                            yaw = Float.parseFloat(data.substring(data.indexOf("*", 0) + 1, data.indexOf("$")));
                                            pitch = Float.parseFloat(data.substring(data.indexOf("$", 0) + 1, data.indexOf("@")));
                                            roll = Float.parseFloat(data.substring(data.indexOf("@", 0) + 1, data.indexOf("#")));
                                            bend = Float.parseFloat(data.substring(data.indexOf("&", 0) + 1, data.indexOf("&", data.indexOf("&", 0) + 1)));
                                            if (Math.abs(bend - bend0) > 30 && getActivity() != null) {
                                                bent++;
                                                NotificationCompat.Builder mBuilder =
                                                        new NotificationCompat.Builder(getActivity())
                                                                .setSmallIcon(R.drawable.ic_test)
                                                                .setLargeIcon(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.ic_test))
                                                                .setContentTitle("Carpal Tunnel Prevention App")
                                                                .setContentText("You have bent your wrist " + Integer.toString(bent) + " times. Consider doing some excercises!");
                                                NotificationManager mNotificationManager =
                                                        (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.

                                                mNotificationManager.notify(1, mBuilder.build());
                                            }
                                        }
                                        updateText("Yaw: " + Float.toString(yaw) + "\nZeroed Yaw: " + Float.toString(yaw - yaw0) + "\nPitch: " + Float.toString(pitch) + "\nZeroed Pitch: " + Float.toString(pitch - pitch0) + "\nRoll: " + Float.toString(roll) + "\nZeroed Roll: " + Float.toString(roll - roll0) + "\nBend: " + Float.toString(bend) + "\nZeroed Bend: " + Float.toString(bend - bend0) + "\nDEBUG: " + data);

                                    }
                                });

                                //mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            } else {
                                readBuffer[readBufferPosition++] = b;
                            }
                        }
                    }
                    /*bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    for (int i = begin; i < bytes; i++) {
                        if (buffer[i] == "\n".getBytes()[0]) {
                            Log.d("connected thread","Sending message to be handled");
                            mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if (i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                        else{
                            Log.d("connected thread","message not handled: " + buffer[i] + buffer);
                            //mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            //begin = i + 1;
                        }
                    }*/
                    /*bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(1, bytes, -1, buffer).sendToTarget();*/
                } catch (IOException e) {
                    Log.d("Connected Thread", "Exception in run of Connected Thread");
                    break;

                }
            }

        }

        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                mmSocket.close();
                Log.d("Connected Socket", "socket canceled");
            } catch (IOException e) {
                Log.d("Connected Socket", "failed to close socket");
            }
        }
    }
}
