package com.sirint.registrodeinfracoes.jsinespclient;

import android.annotation.SuppressLint;
import android.os.Build;

import com.google.gson.Gson;

import org.apache.commons.codec.binary.Hex;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SinespApi {

    private final String secret = "#8.1.0#0KnlVSWHxOih3zKXBWlo";
    @SuppressLint("NewApi")
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public Checkin FireCheckin() {
        String request = "{\n\"userSerialNumber\": " + "0" + ",\n"
                + "\"checkin\":\n" + "{\n" + "\t\"type\": " + "1\n" + "},\n"
                + "\"version\": " + "49\n" + "}";
        Checkin checkin = new Checkin();
        try {
            URL url = new URL("https://android.clients.google.com/checkin");
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            client.setDoOutput(true);
            try (OutputStream os = client.getOutputStream()) {
                byte[] input = request.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Gson gson = new Gson();
                checkin = gson.fromJson(response.toString(), Checkin.class);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return checkin;
    }

    private String RandomString(int length) {
        String result = "";
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int charactersLength = characters.length();
        for (int i = 0; i < length; i++) {
            result += characters.charAt((int) Math.floor(Math.random() * charactersLength));
        }
        return result;
    }

    private String generateToken(String placa) {
        return sha1Hex(placa + secret, placa);
    }

    private String sha1Hex(String secret, String input) {
        String check;
        try {
            Mac sha1_HMAC = Mac.getInstance("HmacSHA1");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA1");
            sha1_HMAC.init(secretKey);
            byte[] hash = sha1_HMAC.doFinal(input.getBytes());
            check = new String(Hex.encodeHex(hash));
        } catch (NoSuchAlgorithmException | InvalidKeyException exception) {
            throw new RuntimeException(exception);
        }
        return check;
    }

    public String FirebaseAuth(Long androidId, Long securityToken) {
        String content = "";
        try {
            String randomHash = RandomString(11);
            String encoded = "sender=905942954488&app=br.gov.sinesp.cidadao.android&device=" + androidId + "&app_ver=49&X-appid=" + randomHash + "&X-subtype=905942954488&X-app_ver=49&cert=daf1d792d60867c52e39c238d9f178c42f35dd98";
            URL url = new URL("https://android.clients.google.com/c2dm/register3");
            HttpURLConnection client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            client.setRequestProperty("app", "br.gov.sinesp.cidadao.android");
            client.setRequestProperty("Authorization", String.format("AidLogin %s:%s", androidId, securityToken));
            try (OutputStream os = client.getOutputStream()) {
                byte[] input = encoded.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);

            }
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                content = response.toString();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        content = content.replace("token=", "");
        return content;
    }

    @SuppressLint("NewApi")
    private byte[] GeraXml(String fToken, String nplaca) throws UnsupportedEncodingException {
        StringBuilder body = new StringBuilder();
        body.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
        body.append("<v:Envelope xmlns:v=\"http://schemas.xmlsoap.org/soap/envelope/\">");
        body.append("<v:Header>");
        body.append("<b>" + Build.MODEL + "</b>");
        body.append("<c>ANDROID</c>");
        body.append("<d>8.1.0</d>");
        body.append("<e>5.1</e>");
        body.append("<f>192.168.0.21</f>");
        body.append("<g>" + generateToken(nplaca) + "</g>");
        body.append("<h>0.0</h>");
        body.append("<i>0.0</i>");
        body.append("<k/>");
        body.append("<l>" + String.format(LocalDateTime.now().format(DATE_FORMAT)) + "</l>");
        body.append("<m>8797e74f0d6eb7b1ff3dc114d4aa12d3</m>");
        body.append("<n>" + fToken + "</n>");
        body.append("</v:Header>");
        body.append("<v:Body>");
        body.append("<n0:getStatus xmlns:n0=\"http://soap.ws.placa.service.sinesp.serpro.gov.br/\" > ");
        body.append("<a>" + nplaca + "</a>");
        body.append("</n0:getStatus>");
        body.append("</v:Body>");
        body.append("</v:Envelope>");
        return body.toString().getBytes("UTF8");
    }

    public Result Consulta(String fToken, String nplaca) throws IOException {
        String content = null;
        Document xml = null;
        /*try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
        }*/

        URL url = new URL("https://cidadao.sinesp.gov.br/sinesp-cidadao/mobile/consultar-placa/v5");
        HttpURLConnection client = (HttpURLConnection) url.openConnection();
        client.setRequestMethod("POST");
        client.setDoOutput(true);
        client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        client.setRequestProperty("User-Agent", "SinespCidadao / 6.0.2.1 CFNetwork / 758.2.8 Darwin / 15.0.0");
        client.setRequestProperty("Host", "cidadao.sinesp.gov.br");
        client.setRequestProperty("Authorization", "Token " + fToken);

        try (OutputStream os = client.getOutputStream()) {
            byte[] input = GeraXml(fToken.substring(0, 11), nplaca);
            os.write(input, 0, input.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            content = response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(content != null) {
            xml = Jsoup.parse(content, "", Parser.xmlParser());
        }
        return parseResult(xml);
    }

    private Result parseResult(Document xml) {
        Result result = new Result();

        result.setReturnCode(Integer.parseInt(xml.select("return > codigoRetorno").first().text()));
        result.setReturnMessage(xml.select("return > mensagemRetorno").first().text());

        if (0 == result.getReturnCode()) {
            result.setStatusCode(Integer.parseInt(xml.select("return > codigoSituacao").first().text()));
            result.setStatusMessage(xml.select("return > situacao").first().text());
            result.setModel(xml.select("return > modelo").first().text());
            result.setBrand(xml.select("return > marca").first().text());
            result.setColor(xml.select("return > cor").first().text());
            result.setYear(Integer.parseInt(xml.select("return > ano").first().text()));
            result.setModelYear(Integer.parseInt(xml.select("return > anoModelo").first().text()));
            result.setPlate(xml.select("return > placa").first().text());
            result.setDate(xml.select("return > data").first().text());
            result.setState(xml.select("return > uf").first().text());
            result.setCity(xml.select("return > municipio").first().text());
            result.setVinCode(xml.select("return > chassi").first().text());
        }

        return result;
    }


}
