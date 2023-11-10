package com.example.trulypresent;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import org.tensorflow.lite.Interpreter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;

public class ModelResults extends Activity {
    private Interpreter tflite;
    private TextView textView;
    //private List<Bitmap> segmentedFaces;

    //private static final int STORAGE_PERMISSION_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_results);
        MappedByteBuffer modelBuffer = loadModelFile();
        if (modelBuffer != null)
            tflite = new Interpreter(modelBuffer);

        String imagePath ="/storage/caf-999/DCIM/IMG-20231030_032037.jpg";
        Bitmap image = BitmapFactory.decodeFile(imagePath);
        float[][] recognitionResult = new float[1][40];
        tflite.run(convertBitmapToFloatArray(image), recognitionResult);
        String recognizedPerson = interpretRecognitionResult(recognitionResult);
        // Display the recognized person's name
        if (!recognizedPerson.isEmpty()) {
            textView = findViewById(R.id.recognizedNamesTextView);
            textView.setText(recognizedPerson);
            textView.setVisibility(View.VISIBLE);
        }
    else
    {
        Toast.makeText(this, "No recognized names to display.", Toast.LENGTH_SHORT).show();
    }
}
    private MappedByteBuffer loadModelFile() {
        try {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd("model.tflite");
            FileInputStream inputStream = new FileInputStream(assetFileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = assetFileDescriptor.getStartOffset();
            long declaredLength = assetFileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the error, e.g., show a message or log the error
            return null; // Return null to indicate that the model failed to load
        }
    }

    private float[][][][] convertBitmapToFloatArray(Bitmap bitmap) {
            int inputSize = 224;
            float[][][][] floatArray = new float[1][inputSize][inputSize][3]; // 3 channels for RGB
            // Resize the Bitmap to match the input size
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, inputSize, inputSize, true);
            // Normalize and populate the float array with pixel values
            for (int y = 0; y < inputSize; y++) {
                for (int x = 0; x < inputSize; x++) {
                    int pixel = resizedBitmap.getPixel(x, y);
                    // Extract RGB values
                    float r = (Color.red(pixel) - 127.5f) / 127.5f;
                    float g = (Color.green(pixel) - 127.5f) / 127.5f;
                    float b = (Color.blue(pixel) - 127.5f) / 127.5f;
                    // Populate the float array with the normalized values
                    int index = (y * inputSize + x) * 3;
                    floatArray[0][y][x][0] = r;
                    floatArray[0][y][x][1] = g;
                    floatArray[0][y][x][2] = b;
                }
            }

            return floatArray;
    }

    private String interpretRecognitionResult(float[][] recognitionResult) {
        if (recognitionResult != null && recognitionResult.length > 0) {
            // Find the class with the highest probability
            int maxClassIndex = 0;
            float maxClassProbability = recognitionResult[0][0];

            for (int i = 1; i < recognitionResult[0].length; i++) {
                if (recognitionResult[0][i] > maxClassProbability) {
                    maxClassIndex = i;
                    maxClassProbability = recognitionResult[0][i];
                }
            }
            String[] classLabels = {"s1", "s2", "s3", "s4", "s5", "s6", "s7", "s8", "s9", "s10", "s11", "s12", "s13", "s14", "s15", "s16", "s17", "s18", "s19", "s20", "s21", "s22", "s23", "s24", "s25", "s26", "s27", "s28", "s29", "s30", "s31", "s32", "s33", "s34", "s35", "s36", "s37", "s38", "s39", "s40"};
            String recognizedLabel = classLabels[maxClassIndex];

            return recognizedLabel;
        }else{
        return "Unknown";
    }
}
}