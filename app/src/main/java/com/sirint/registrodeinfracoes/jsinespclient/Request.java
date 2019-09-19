  package com.sirint.registrodeinfracoes.jsinespclient;

  import android.util.Xml;

  import com.google.api.client.util.DateTime;
  import com.google.common.base.Utf8;

  import java.io.ByteArrayOutputStream;
  import java.io.UnsupportedEncodingException;
  import java.net.URLEncoder;
  import java.util.Iterator;
  import javax.xml.bind.JAXBContext;
  import javax.xml.bind.JAXBException;
  import javax.xml.bind.Marshaller;
  import javax.xml.bind.annotation.XmlElement;
  import javax.xml.bind.annotation.XmlRootElement;
  import javax.xml.bind.annotation.XmlTransient;
  import javax.xml.namespace.QName;
  import javax.xml.parsers.DocumentBuilderFactory;
  import javax.xml.parsers.ParserConfigurationException;
  import javax.xml.soap.MessageFactory;
  import javax.xml.soap.SOAPBody;
  import javax.xml.soap.SOAPEnvelope;
  import javax.xml.soap.SOAPException;
  import javax.xml.soap.SOAPHeader;
  import javax.xml.soap.SOAPMessage;
  import javax.xml.transform.OutputKeys;
  import javax.xml.transform.Transformer;
  import javax.xml.transform.TransformerException;
  import javax.xml.transform.TransformerFactory;
  import javax.xml.transform.stream.StreamResult;
  import org.w3c.dom.Document;

  /**
   *
   * @author Lucas Souza [sorack@gmail.com]
   */
  @XmlRootElement(name = "getStatus", namespace = "http://soap.ws.placa.service.sinesp.serpro.gov.br/")
  public class Request {

    private final String prefix = "v";

    private String plate;

    private String device;
    private Double latitude;
    private String operationalSystem;
    private String majorVersion;
    private String minorVersion;
    private String ip;
    private String token;
    private String uuid;
    private Double longitude;
    private String date;
    private String hash;
    private String fToken;

    public Request() {
      this.device = "motorola XT1710-01";
      this.operationalSystem = "ANDROID";
      this.majorVersion = "8.1.0";
      this.minorVersion = "5.1";
      this.ip = "192.168.0.120";
      this.hash = "8797e74f0d6eb7b1ff3dc114d4aa12d3";
    }
    
    @XmlElement(name = "a")
    public String getPlate() {
      return plate;
    }

    public void setPlate(String plate) {
      this.plate = plate;
    }

    @XmlTransient
    public String getDevice() {
      return device;
    }

    public void setDevice(String device) {
      this.device = device;
    }

    @XmlTransient
    public Double getLatitude() {
      return latitude;
    }

    public void setLatitude(Double latitude) {
      this.latitude = latitude;
    }

    @XmlTransient
    public String getOperationalSystem() {
      return operationalSystem;
    }

    public void setOperationalSystem(String operationalSystem) {
      this.operationalSystem = operationalSystem;
    }

    @XmlTransient
    public String getMajorVersion() {
      return majorVersion;
    }

    public void setMajorVersion(String majorVersion) {
      this.majorVersion = majorVersion;
    }

    @XmlTransient
    public String getMinorVersion() {
      return minorVersion;
    }

    public void setMinorVersion(String minorVersion) {
      this.minorVersion = minorVersion;
    }

    @XmlTransient
    public String getIp() {
      return ip;
    }

    public void setIp(String ip) {
      this.ip = ip;
    }

    @XmlTransient
    public String getToken() {
      return token;
    }

    public void setToken(String token) {
      this.token = token;
    }

    @XmlTransient
    public String getUuid() {
      return uuid;
    }

    public void setUuid(String uuid) {
      this.uuid = uuid;
    }

    @XmlTransient
    public Double getLongitude() {
      return longitude;
    }

    public void setLongitude(Double longitude) {
      this.longitude = longitude;
    }

    @XmlTransient
    public String getDate() {
      return date;
    }

    public void setDate(String date) {
      this.date = date;
    }

    @XmlTransient
    public String getHash() {
      return hash;
    }

    public void setHash(String hash) {
      this.hash = hash;
    }
    
    @XmlTransient
    public String getfToken() {
        return fToken;
    }

    public void setfToken(String fToken) {
        this.fToken = fToken;
    }

  /*
    public byte[] toXML(String plate ) {
      StringBuilder body = new StringBuilder();
      body.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
      body.append("<v:Envelope xmlns:i=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:d=\"http://www.w3.org/2001/XMLSchema\" xmlns:c=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:v=\"http://schemas.xmlsoap.org/soap/envelope/\" >");
      body.append("<v:Header>");
      body.append("<b>motorola XT1635-02</b>");
      body.append("<c>ANDROID</c>");
      body.append("<d>8.1.0</d>");
      body.append("<e>5.1</e>");
      body.append("<f>192.168.0.100</f>");
      body.append("<g>" + token + "</g>");
      body.append("<h>0</h>");
      body.append("<i>0</i>");
      body.append("<k/>");
      body.append("<l>" + date + "</l>");
      body.append("<m>8797e74f0d6eb7b1ff3dc114d4aa12d3</m>");
      body.append("<n>" + fToken + "</n>");
      body.append("</v:Header>");
      body.append("<v:Body>");
      body.append("<n0:getStatus xmlns:n0=\"http://soap.ws.placa.service.sinesp.serpro.gov.br/\" > ");
      body.append("<a>" + plate + "</a>");
      body.append("</n0:getStatus>");
      body.append("</v:Body>");
      body.append("</v:Envelope>");

      return toXML().toString().getBytes("UTF8");
    }*/



    private void fillHeader(SOAPHeader soapHeader) throws SOAPException {
      soapHeader.setPrefix(this.prefix);
      soapHeader.addHeaderElement(new QName(soapHeader.getNamespaceURI(), "b")).setValue(this.device);
      soapHeader.addHeaderElement(new QName(soapHeader.getNamespaceURI(), "c")).setValue(this.operationalSystem);
      soapHeader.addHeaderElement(new QName(soapHeader.getNamespaceURI(), "d")).setValue(this.majorVersion);
      soapHeader.addHeaderElement(new QName(soapHeader.getNamespaceURI(), "e")).setValue(this.minorVersion);
      soapHeader.addHeaderElement(new QName(soapHeader.getNamespaceURI(), "f")).setValue(this.ip);
      soapHeader.addHeaderElement(new QName(soapHeader.getNamespaceURI(), "g")).setValue(this.token);
      soapHeader.addHeaderElement(new QName(soapHeader.getNamespaceURI(), "h")).setValue("0");
      soapHeader.addHeaderElement(new QName(soapHeader.getNamespaceURI(), "i")).setValue("0");
      soapHeader.addHeaderElement(new QName(soapHeader.getNamespaceURI(), "j")).setValue("");
      soapHeader.addHeaderElement(new QName(soapHeader.getNamespaceURI(), "k")).setValue("");
      soapHeader.addHeaderElement(new QName(soapHeader.getNamespaceURI(), "l")).setValue(this.date);
      soapHeader.addHeaderElement(new QName(soapHeader.getNamespaceURI(), "m")).setValue(this.hash);
      soapHeader.addHeaderElement(new QName(soapHeader.getNamespaceURI(), "n")).setValue(this.fToken);
    }
  }
