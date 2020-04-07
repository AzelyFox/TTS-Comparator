package kr.devx.ttscomparator;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * https://github.com/NaverCloudPlatform/android-ai-sample/blob/master/app/src/main/java/com/ncp/ai/demo/process/CssProc.java
 */

public class CssProc {

	public static void main(Context context, String msg, String speaker, int speed, String clientId, String clientSecret) {

		try {
			String text = URLEncoder.encode(msg, "UTF-8");
			String apiURL = "https://naveropenapi.apigw.ntruss.com/voice/v1/tts";
			URL url = new URL(apiURL);
			HttpURLConnection con = (HttpURLConnection)url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("X-NCP-APIGW-API-KEY-ID", clientId);
			con.setRequestProperty("X-NCP-APIGW-API-KEY", clientSecret);
			String postParams = "speaker=" + speaker + "&speed=" + speed + "&text=" + text;
			System.out.println(postParams);
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(postParams);
			wr.flush();
			wr.close();
			int responseCode = con.getResponseCode();
			BufferedReader br;
			System.out.println("## response code : "+responseCode);
			if(responseCode == 200) { // 정상 호출
				InputStream is = con.getInputStream();
				int read = 0;
				byte[] bytes = new byte[1024];

				File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator, "NCP");

				if(!dir.exists()){
					dir.mkdirs();
				}

				String tempname = "csstemp";
				File f = new File( context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "NCP" + File.separator + tempname + ".mp3");
				f.createNewFile();
				OutputStream outputStream = new FileOutputStream(f);
				while ((read =is.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);
				}
				is.close();

				String pathToFile = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + File.separator + "NCP" + File.separator + tempname + ".mp3";
				MediaPlayer audioPlay = new MediaPlayer();
				audioPlay.setDataSource(pathToFile);
				audioPlay.prepare();
				audioPlay.start();

				System.out.println("## file path : " + pathToFile);

			} else {  // 에러 발생
				br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
				String inputLine;
				StringBuffer response = new StringBuffer();
				while ((inputLine = br.readLine()) != null) {
					response.append(inputLine);
				}
				br.close();
				System.out.println(response.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static class NaverTTSTask extends AsyncTask<String, String, String> {

		private Context context;

		public NaverTTSTask(Context context) {
			this.context = context;
		}

		@Override
		public String doInBackground(String... strings) {
			CssProc.main(context, strings[0], strings[1], Integer.parseInt(strings[2]), strings[3], strings[4]);
			return null;
		}
	}

}