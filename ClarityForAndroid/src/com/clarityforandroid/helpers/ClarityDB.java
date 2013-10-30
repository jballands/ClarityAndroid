package com.clarityforandroid.helpers;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.clarityforandroid.R;

/**
 * Methods that make RESTful calls to the Clarity.db database.
 * 
 * @author Jonathan Ballands
 * @version 1.0
 */
public class ClarityDB {

	public static void testQuery(Context context) {
		Bitmap img = BitmapFactory.decodeResource(context.getResources(), R.drawable.add_patient_image);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String imageInString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://clarity-db.appspot.com/howmany");
        
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("msg", imageInString));
        
        try {
			httpPost.setEntity(new UrlEncodedFormEntity(params));
			httpClient.execute(httpPost);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}