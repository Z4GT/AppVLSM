package com.example.appvlsm;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Typeface.BOLD;

public class VLSMCalculator {

    public SpannableStringBuilder calcularSoloConHosts(String red, int hostsRequeridos) {
        int bits = 0;
        int totalHosts = 0;
        while (totalHosts < hostsRequeridos + 2) {
            bits++;
            totalHosts = (int) Math.pow(2, bits);
        }
        int cidr = 32 - bits;
        return calcularDetalles(red, cidr);
    }

    public SpannableStringBuilder calcularSoloConMascara(String red, int cidr) {
        return calcularDetalles(red, cidr);
    }

    public SpannableStringBuilder calcularConMascaraYHosts(String red, int cidr, int hostsRequeridos) {
        int maxHosts = (int) Math.pow(2, 32 - cidr) - 2;
        if (hostsRequeridos > maxHosts) {
            return makeBoldText("⚠️ La cantidad de hosts requerida (" + hostsRequeridos +
                    ") no es posible con la máscara /" + cidr +
                    ".\nMáximo permitido: " + maxHosts + " hosts.\n");
        }
        return calcularDetalles(red, cidr);
    }

    public SpannableStringBuilder calcularDetalles(String red, int cidr) {
        SpannableStringBuilder sb = new SpannableStringBuilder();

        if (cidr < 0 || cidr > 32) {
            return makeBoldText("Máscara CIDR inválida.");
        }

        String[] partes = red.split("\\.");
        if (partes.length != 4) {
            return makeBoldText("Formato de dirección de red inválido.");
        }

        int ip = (Integer.parseInt(partes[0]) << 24) |
                (Integer.parseInt(partes[1]) << 16) |
                (Integer.parseInt(partes[2]) << 8) |
                Integer.parseInt(partes[3]);

        int mascara = 0xffffffff << (32 - cidr);
        int direccionRed = ip & mascara;
        int direccionBroadcast = direccionRed | ~mascara;

        String redStr = intAIP(direccionRed);
        String broadcastStr = intAIP(direccionBroadcast);
        String primerIP = intAIP(direccionRed + 1);
        String ultimaIP = intAIP(direccionBroadcast - 1);

        int totalHosts = (int) Math.pow(2, 32 - cidr);
        int hostsUsables = totalHosts - 2;

        String mascaraSubred = cidrAMascara(cidr);
        String wildcard = cidrAWildcard(cidr);
        String mascaraBinaria = formatearBinario(mascara);
        String claseIP = obtenerClaseIP(partes[0]);
        String tipoIP = obtenerTipoIP(partes[0]);

        addLine(sb, "Dirección de Red: ", redStr);
        addLine(sb, "Rango de IPs Usables: ", primerIP + " - " + ultimaIP);
        addLine(sb, "Dirección de Broadcast: ", broadcastStr);
        addLine(sb, "Número Total de Hosts: ", String.valueOf(totalHosts));
        addLine(sb, "Número de Hosts Usables: ", String.valueOf(hostsUsables));
        addLine(sb, "Máscara de Subred: ", mascaraSubred);
        addLine(sb, "Máscara Wildcard: ", wildcard);
        addLine(sb, "Máscara Binaria: ", mascaraBinaria);
        addLine(sb, "Clase de IP: ", claseIP);
        addLine(sb, "Notación CIDR: ", "/" + cidr);
        addLine(sb, "Tipo de IP: ", tipoIP);

        return sb;
    }

    private void addLine(SpannableStringBuilder sb, String label, String value) {
        int start = sb.length();
        sb.append(label);
        sb.setSpan(new StyleSpan(BOLD), start, start + label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        sb.append(value).append("\n");
    }

    private SpannableStringBuilder makeBoldText(String text) {
        SpannableStringBuilder sb = new SpannableStringBuilder(text);
        sb.setSpan(new StyleSpan(BOLD), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    private String intAIP(int ip) {
        return ((ip >> 24) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                (ip & 0xFF);
    }

    private String cidrAMascara(int cidr) {
        int mascara = 0xffffffff << (32 - cidr);
        return intAIP(mascara);
    }

    private String cidrAWildcard(int cidr) {
        int mascara = ~(0xffffffff << (32 - cidr));
        return intAIP(mascara);
    }

    private String formatearBinario(int mascara) {
        String binario = String.format("%32s", Integer.toBinaryString(mascara)).replace(' ', '0');
        return binario.substring(0,8) + "." +
                binario.substring(8,16) + "." +
                binario.substring(16,24) + "." +
                binario.substring(24,32);
    }

    private String obtenerClaseIP(String primerOctetoStr) {
        int primerOcteto = Integer.parseInt(primerOctetoStr);
        if (primerOcteto >= 0 && primerOcteto <= 127) return "A";
        if (primerOcteto >= 128 && primerOcteto <= 191) return "B";
        if (primerOcteto >= 192 && primerOcteto <= 223) return "C";
        return "Otra";
    }

    private String obtenerTipoIP(String primerOctetoStr) {
        int primerOcteto = Integer.parseInt(primerOctetoStr);
        if (primerOcteto == 10 ||
                (primerOcteto == 172) ||
                (primerOcteto == 192)) {
            return "Privada";
        }
        return "Pública";
    }
}