package com.example.appvlsm;

import java.util.ArrayList;
import java.util.List;

public class VLSMCalculator {

    public String calcularSoloConHosts(String red, int hostsRequeridos) {
        int bits = 0;
        int totalHosts = 0;
        while (totalHosts < hostsRequeridos + 2) {
            bits++;
            totalHosts = (int) Math.pow(2, bits);
        }
        int cidr = 32 - bits;
        return calcularSoloConMascara(red, cidr);
    }

    public String calcularSoloConMascara(String red, int cidr) {
        return calcularDetalles(red, cidr);
    }

    public String calcularConMascaraYHosts(String red, int cidr, int hostsRequeridos) {
        int maxHosts = (int) Math.pow(2, 32 - cidr) - 2;
        if (hostsRequeridos > maxHosts) {
            return "⚠️ La cantidad de hosts requerida (" + hostsRequeridos + ") no es posible con la máscara /" + cidr + ".\nMáximo permitido: " + maxHosts + " hosts.";
        }
        return calcularDetalles(red, cidr);
    }

    public String calcularDetalles(String red, int cidr) {
        if (cidr < 0 || cidr > 32) {
            return "Máscara CIDR inválida.";
        }

        String[] partes = red.split("\\.");
        if (partes.length != 4) {
            return "Formato de dirección de red inválido.";
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

        StringBuilder sb = new StringBuilder();
        sb.append("Dirección de Red: ").append(redStr).append("\n");
        sb.append("Rango de IPs Usables: ").append(primerIP).append(" - ").append(ultimaIP).append("\n");
        sb.append("Dirección de Broadcast: ").append(broadcastStr).append("\n");
        sb.append("Número Total de Hosts: ").append(totalHosts).append("\n");
        sb.append("Número de Hosts Usables: ").append(hostsUsables).append("\n");
        sb.append("Máscara de Subred: ").append(mascaraSubred).append("\n");
        sb.append("Máscara Wildcard: ").append(wildcard).append("\n");
        sb.append("Máscara Binaria: ").append(mascaraBinaria).append("\n");
        sb.append("Clase de IP: ").append(claseIP).append("\n");
        sb.append("Notación CIDR: /").append(cidr).append("\n");
        sb.append("Tipo de IP: ").append(tipoIP).append("\n\n");
        return sb.toString();
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