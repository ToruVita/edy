package jplas.answer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


/**
 * @author n.ishihara
 * 外部プログラムの実行クラス
 */
public class JplasRunner {
	/**
	 * 実行時間の制限
	 */
	protected static int timeoutSeconds = 10;
	public int getTimeoutSeconds() {
		return timeoutSeconds;
	}
	public void setTimeoutSeconds(int timeoutSeconds) {
		this.timeoutSeconds = timeoutSeconds;
	}
	static Stopper stopper = new Stopper();
	/**
	 * @param dir　実行用フォルダ
	 * @param commandLine コマンドライン
	 * @return 実行結果
	 */
	public static String jplasRun(File dir/*exec folder*/, List<String> commandLine){
//		commandLine.forEach(s->{
//			System.out.print(s+" ");
//		});
//		System.out.println(System.lineSeparator() + dir.getAbsolutePath());
		StringBuffer bf = new StringBuffer();
		ProcessBuilder pb = new ProcessBuilder(commandLine);
		pb.directory(dir);
		pb.redirectErrorStream(true);
		try {
			Process process = pb.start();
			Stopper stopper = new Stopper();
			stopper.setProcess(process);
			Timer timer = new Timer();
			timer.schedule(stopper, 5000L);
			boolean flg = process.waitFor(5, TimeUnit.SECONDS);
			InputStream is = process.getInputStream();
			bf.append(printInputStream(is));
			InputStream es = process.getErrorStream();
			bf.append(printInputStream(es));
//			if(ret>10){
//				bf.append(String.format("Over %d sec.", timeoutSeconds));
//			}
			bf.append("\n");
			if(flg) {
				process.destroy();
			}
			timer.cancel();
		} catch (IOException e) {
			bf.append(e.toString());
		} catch (InterruptedException e) {
			bf.append(e.toString());
		}finally{
		}
		return bf.toString();
	}
	/**
	 * 標準出力とエラー出力を文字列バッファーに収めるためのメソッド
	 * BufferedReaderを使用している
	 * 実行時の文字コードは基本的にはOSに依存するものもあるので，"JISAutodetect"の指定も必要．
	 * @param is 入力(process.getInputStream())およびprocess.getErrorStream()
	 * @return 出力文字列
	 * @throws UnsupportedEncodingException 
	 * @throws IOException
	 */
	private static String printInputStream(InputStream is) throws UnsupportedEncodingException {
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "JISAutodetect"));
		StringBuffer bf = new StringBuffer();
		try {
			for (;;) {
				String line = br.readLine();
				if (line == null) break;
				bf.append(line + "\n");
			}
		} catch (IOException e) {
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				bf.append(e);
			}
		}
		return bf.toString();
	}
}
class Stopper extends TimerTask {

	Process process;
	public void setProcess(Process process) {
		this.process = process;
	}
	@Override
	public void run() {
		process.destroy();
	}
}