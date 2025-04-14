package com.example.appvlsm;

import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText networkInput;
    private EditText hostsInput;
    private EditText maskInput;
    private Button calculateButton;
    private TextView resultText;
    private VLSMCalculator calculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkInput = findViewById(R.id.networkInput);
        hostsInput = findViewById(R.id.hostsInput);
        maskInput = findViewById(R.id.maskInput);
        calculateButton = findViewById(R.id.calculateButton);
        resultText = findViewById(R.id.resultText);
        calculator = new VLSMCalculator();

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String network = networkInput.getText().toString();
                String hostsStr = hostsInput.getText().toString();
                String maskStr = maskInput.getText().toString();

                if (network.isEmpty()) {
                    resultText.setText("Por favor ingrese la dirección de red.");
                    return;
                }

                SpannableStringBuilder resultado;

                if (!maskStr.isEmpty()) {
                    try {
                        int cidr = Integer.parseInt(maskStr);
                        if (!hostsStr.isEmpty()) {
                            int hosts = Integer.parseInt(hostsStr);
                            resultado = calculator.calcularConMascaraYHosts(network, cidr, hosts);
                        } else {
                            resultado = calculator.calcularSoloConMascara(network, cidr);
                        }
                    } catch (NumberFormatException e) {
                        resultText.setText("Máscara o número de hosts inválido.");
                        return;
                    }
                } else if (!hostsStr.isEmpty()) {
                    try {
                        int hosts = Integer.parseInt(hostsStr);
                        resultado = calculator.calcularSoloConHosts(network, hosts);
                    } catch (NumberFormatException e) {
                        resultText.setText("Número de hosts inválido.");
                        return;
                    }
                } else {
                    resultText.setText("Por favor ingrese número de hosts o máscara de subred.");
                    return;
                }

                resultText.setText(resultado, TextView.BufferType.SPANNABLE);

            }
        });
    }
}