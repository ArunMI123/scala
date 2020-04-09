package com.kumaran.tac.framework.selenium.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.net.ssl.SSLContext;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import java.io.File;
import java.io.FileInputStream;

public class RestUtil {

	public static String RestClientGet(String url) throws IOException {

		StringBuilder result = new StringBuilder("");
		String output =null;
		if (url.contains("http://")) {
			
			try {
				URL restUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) restUrl.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
				if (conn.getResponseCode() != 200) {
					System.out.println(conn.getResponseCode());
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				while ((output = br.readLine()) != null) {
					result.append(output).append("\n");
				}
				conn.disconnect();
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				throw new IOException(
						"Couldn't retrive data from given testId because server down or Given TestId not matched==>" + url);

			}
			return result.toString();
		} else if(url.contains("https://")){
			try {
				SSLContextBuilder SSLBuilder = SSLContexts.custom();

				// ********** starting of code for runnable jar **********
				// Loading the Keystore file
				Properties p = new Properties();
				String externalFileName = System.getProperty("config.location");
				InputStream fin = new FileInputStream(new File(externalFileName));
				p.load(fin);
				File file = new File(p.getProperty("https"));
				// ********** ending of code for runnable jar **********
				
				//below line is for debugging
//				File file = new File("kumaran_2019.jks");
				
				SSLBuilder = SSLBuilder.loadTrustMaterial(file, "Welcome@321".toCharArray());

				// Building the SSLContext usiong the build() method
				SSLContext sslcontext = SSLBuilder.build();

				// Creating SSLConnectionSocketFactory object
				SSLConnectionSocketFactory sslConSocFactory = new SSLConnectionSocketFactory(sslcontext,
						new NoopHostnameVerifier());

				// Creating HttpClientBuilder
				HttpClientBuilder clientbuilder = HttpClients.custom();

				// Setting the SSLConnectionSocketFactory
				clientbuilder = clientbuilder.setSSLSocketFactory(sslConSocFactory);

				// Building the CloseableHttpClient
				CloseableHttpClient httpclient = clientbuilder.build();

				// Creating the HttpGet request
				HttpGet httpget = new HttpGet(url);

				// Executing the request
				HttpResponse httpresponse = httpclient.execute(httpget);
				StatusLine statusLine = httpresponse.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					httpresponse.getEntity().writeTo(out);
					output = out.toString();
					System.out.println(output);
					out.close();
				} else {
					httpresponse.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch(Exception er){
				er.printStackTrace();
			}
			return output;
		}else{
			return "";
		}
		
		
	}

	public static String RestClientPost(String url, String data) {
		StringBuffer jsonString = new StringBuffer();
		String output = null;
		if(url.contains("http://")){
			try {
				URL restUrl = new URL(url);
				HttpURLConnection conn = (HttpURLConnection) restUrl.openConnection();

				conn.setDoInput(true);
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Accept", "application/json");
				conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
				writer.write(data);
				writer.close();
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
				
				String line;
				while ((line = br.readLine()) != null) {
					jsonString.append(line);
				}
				br.close();
				conn.disconnect();
				return jsonString.toString();
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			}
			
		}else if(url.contains("https://")){
			
			try {
				SSLContextBuilder SSLBuilder = SSLContexts.custom();
				// ********** starting of code for runnable jar **********
				// Loading the Keystore file
				Properties p = new Properties();
				String externalFileName = System.getProperty("config.location");
				InputStream fin = new FileInputStream(new File(externalFileName));
				p.load(fin);
				File file = new File(p.getProperty("https"));
				// ********** ending of code for runnable jar **********
				
				//below line is for debugging
				//File file = new File("kumaran_2019.jks");
				
				SSLBuilder = SSLBuilder.loadTrustMaterial(file, "Welcome@321".toCharArray());

				// Building the SSLContext usiong the build() method
				SSLContext sslcontext = SSLBuilder.build();

				// Creating SSLConnectionSocketFactory object
				SSLConnectionSocketFactory sslConSocFactory = new SSLConnectionSocketFactory(sslcontext,
						new NoopHostnameVerifier());

				// Creating HttpClientBuilder
				HttpClientBuilder clientbuilder = HttpClients.custom();

				// Setting the SSLConnectionSocketFactory
				clientbuilder = clientbuilder.setSSLSocketFactory(sslConSocFactory);

				// Building the CloseableHttpClient
				CloseableHttpClient httpclient = clientbuilder.build();

				// Creating the HttpGet request
				HttpPost httppost = new HttpPost(url);
				httppost.setHeader("Accept", "application/json");
				httppost.setHeader("Content-Type", "application/json; charset=UTF-8");
				httppost.setEntity(new StringEntity(data));
				// Executing the request
				HttpResponse httpresponse = httpclient.execute(httppost);
				StatusLine statusLine = httpresponse.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					httpresponse.getEntity().writeTo(out);
					output = out.toString();
					out.close();
				} else {
					httpresponse.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return output;
		}else{
			return "";
		}
		
		
	}

}
