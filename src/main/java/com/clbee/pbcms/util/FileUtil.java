package com.clbee.pbcms.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import com.clbee.pbcms.util.StringUtil;


public class FileUtil {

    /**
     * 파일을 카피하는메소드이며 양쪽이 디렉토리이면 디렉토리를생성하고 원래소스가 파일이고카피하려는곳이 디렉토리이면 생성을하고 파일을그하위로
     * 생성한다.
     *
     * @param source
     *            카피를하고자하는 원본파일.
     * @param dest
     *            카피를 한후의 목적파일.
     * @exception 파일을
     *                읽다가 에러가 발생하면 java.io.IOException를 throw함.
     */
    public static void copy(File source, File dest) throws IOException {
        File parentFile = null;

        if (source.isDirectory()) {
            if (dest.isDirectory()) {
            } else if (dest.exists()) {
                //이런! 이미 파일이 존재하고있으면 어떻게 하나??
                throw new IOException("이미 존재하는파일입니다. --> '" + dest + "'.");
            } else {
                dest = new File(dest, source.getName());
                dest.mkdirs();
            }
        }

        if (source.isFile() && dest.isDirectory()) {
            parentFile = new File(dest.getAbsolutePath());
            dest = new File(dest, source.getName());
        } else {
            parentFile = new File(dest.getParent());
        }

        parentFile.mkdirs();

        if (!source.canRead()) {
            throw new IOException("Cannot read file '" + source + "'.");
        }

        if (dest.exists() && (!dest.canWrite())) {
            throw new IOException("Cannot write to file '" + dest + "'.");
        }

        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                    source));
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(
                    dest));
        byte[] buffer = new byte[1024];
        int read = -1;

        while ((read = in.read(buffer, 0, 1024)) != -1)
            out.write(buffer, 0, read);

        out.flush();
        out.close();
        in.close();
    }

    /**
     * 파일을 한쪽에서 다른쪽으로 복사를 하는 메소드.
     *
     * @param fileName
     *            파일의 이름 ( 경로가 아님 )
     * @param fromDir
     *            원본파일이 있는디렉토리
     * @param toDir
     *            복사한 파일이 있어야할 디렉토리
     * @exception 파일을
     *                읽다가 에러가 발생하면 java.io.IOException를 throw함.
     */
    public static void copy(String fileName, String fromDir, String toDir)
        throws IOException {
        copy(new File(fromDir + File.separator + fileName),
            new File(toDir + File.separator + fileName));
    }

    /**
     * 파일에 대한 Stream을 인자로 주면 Copy를 하는 메소드.
     *
     * @param in
     *            원본파일에대한 스트림
     * @param out
     *            카피후 저장하려는 파일에대한 스트림
     * @exception 파일을
     *                읽다가 에러가 발생하면 java.io.IOException를 throw함.
     */
    public static void copy(InputStream in, OutputStream out)
        throws IOException {
        BufferedInputStream bin = new BufferedInputStream(in);
        BufferedOutputStream bout = new BufferedOutputStream(out);
        byte[] buffer = new byte[1024];
        int read = -1;

        while ((read = bin.read(buffer, 0, 1024)) != -1)
            bout.write(buffer, 0, read);

        bout.flush();
        bout.close();
        bin.close();
    }

    /**
     * 절대경로이든지 상대경로이든지 가리지않고 파일객체에서 InputStream을 가져옴.
     *
     * @param file
     *            읽어야할 파일객체
     * @param c
     *            인스턴스화된 클래스객체
     * @return 파일에대한 InputStream
     * @exception 파일을
     *                찾지못하면 java.io.FileNotFoundException을 throw함.
     */
    public static InputStream getInputStream(File file, Class c)
        throws FileNotFoundException {
        InputStream rtn;
        String s;

        if (file != null) {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                s = file.toString();

                int i = s.indexOf(File.separator);

                if (i >= 0) {
                    s = s.substring(i);
                    s = StringUtil.replace("\\", "/", s);

                    if ((rtn = c.getResourceAsStream(s)) != null) {
                        return rtn;
                    }
                }

                throw e;
            }
        }

        return null;
    }

    /**
     * 파일 목록을 받아 모두 삭제 처리한다.
     * @author Administrator
     * @param  list
     * @return cnt - 삭제된 갯수
     * @date   2007. 06. 04
     */
    public static int delete(List fileList) {

        int cnt = 0;

        if(fileList.size()>0) {

            File f = null;

            for(int i=0 ; i<fileList.size() ; i++) {
                Map file = (Map)fileList.get(i);

                f = new File(file.get("FILE_DIR") + "" + file.get("FILE_SAVED_NAME"));

                if( FileUtil.delete( f ) ) {
                    cnt++;
                }
            }
        }

        return cnt;
    }

    /**
     * 디렉토리인경우에 하위의 모든파일을 삭제하고 자기자신도 삭제됨. 파일일경우에는 자기자신이 삭제가됨.
     *
     * @param file
     *            the file (or directory) to delete.
     * @return 파일이나 디렉토리의 삭제여부.
     */
    public static boolean delete(File file) {
    	//System.out.println("경로가???====="+file.getAbsolutePath()+"@@@");
    	//System.out.println("파일이 있나??====="+file.exists());
        if (file.exists()) {
        	//System.out.println("파일이 있네====="+file.exists());
            if (file.isDirectory()) {
            	//System.out.println("디렉토리인가??====="+file.isDirectory());
                if (clean(file)) {
                    return file.delete();
                } else {
                    return false;
                }
            } else {
            	//System.out.println("그냥 파일인듯====="+file.isDirectory());
                return file.delete();
            }
        }

        return true;
    }

    /**
     * 디렉토리인경우에 하위의 모든파일을 삭제하고 true를 넘겨줌. 파일일경우에는 그냥 true를 넘김.
     *
     * @param file
     *            the directory to clean
     */
    public static boolean clean(File file) {
        if (file.isDirectory()) {
            String[] filen = file.list();

            for (int i = 0; i < filen.length; i++) {
                File subfile = new File(file, filen[i]);

                if ((subfile.isDirectory()) && (!clean(subfile))) {
                    return false;
                } else if (!subfile.delete()) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * 디렉토리인경우 하위의 파일들릐 리스트를 만들어서 넘겨줌.
     *
     * @param dir
     *            디렉토리인 파일객체
     * @return 파일리스트 File[] <-- 파일의배열 존재하지않으면 null이 return됨.
     */
    public static File[] listFiles(File dir) {
        String[] ss = dir.list();

        if (ss == null) {
            return null;
        }

        int n = ss.length;
        File[] fs = new File[n];

        for (int i = 0; i < n; i++) {
            fs[i] = new File(dir.getPath(), ss[i]);
        }

        return fs;
    }

    /**
     * 파라미터로 넘겨진 경로가 디렉토리인지아닌지의 여부를 넘겨줌
     *
     * @param path
     *            파일의 경로
     * @return 디렉토리인지의 여부
     */
    public static boolean isDirectory(String path) {
        boolean dir = false;

        if (path != null) {
            File file = new File(path);

            dir = file.isDirectory();
        }

        return dir;
    }

    /**
     * 파라미터로 넘겨진 경로가 파일인지아닌지의 여부를 넘겨줌
     *
     * @param path
     *            파일의 경로
     * @return 파일인지의 여부
     */
    public static boolean isFile(String path) {
        boolean file = false;

        if (path != null) {
            File f = new File(path);

            file = f.isFile();
        }

        return file;
    }

    /**
     * 시스템에 관계없고 절대경로이거나 상대경로로 들어와도 관계없이 현재 시스템에 맞는 정규경로를 반환함.
     *
     * @param in
     *            파일까지포함된 경로 윈도우나 유닉스경로인지는 상관하지않음.
     * @return 현재 시스템에 맞는 정규경로.
     */
    public static String toCanonicalPath(final String in) {
        final String DOT = new String() + '.';
        String current = FileUtil.toCurrentPath(in);
        String out = new String(current);
        int index = -1;

        index = in.indexOf(DOT + DOT);

        if (index < 0) {
            index = current.indexOf(File.separator + '.');
        }

        if (index < 0) {
            index = current.indexOf('.' + File.separator);
        }

        if ((index > -1) || in.startsWith(DOT) || in.endsWith(DOT)) {
            File file = new File(current);

            try {
                out = file.getCanonicalPath();
            } catch (Exception e) {
                out = current;
                e.printStackTrace();
            }
        }

        return out;
    }

    /**
     * 자바에서 인식가능한 경로로 변환하여줌.
     *
     * @param in
     *            파일에 대한 경로
     */
    public static String toJavaPath(final String in) {
        String path = new String(in);

        path = FileUtil.toCurrentPath(path);

        return path.replace('\\', '/');
    }

    /**
     * 현재 시스템에 맞는 경로로 변환하여줌
     *
     * @param 파일에
     *            대한 경로
     * @return 현재 시스템에 맞는 경로
     */
    public static String toCurrentPath(String path) {
        String cPath = path;
        File file;

        if (File.separatorChar == '/') {
            cPath = FileUtil.toShellPath(cPath);
        } else {
            cPath = FileUtil.toWindowsPath(cPath);
        }

        file = new File(cPath);

        // Add default drive
        file = new File(file.getAbsolutePath());

        if (file.exists()) {
            cPath = file.getAbsolutePath();
        }

        return cPath.trim();
    }

    /**
     * 파일까지 포함된는경로에서 포함되어진 file seperators를 유닉스에 맞게 바꾸어줌. 또한 윈도우경로인 경우 시작부분이
     * 드라이브 문자에서이면 유닉스의 루트로 바꾸어줌.
     *
     * @param path
     *            파일까지포함된 경로 윈도우나 유닉스경로인지는 상관하지않음.
     * @return Cygnus shell에 맞는 경로.
     */
    public static String toShellPath(String inPath) {
        StringBuffer path = new StringBuffer();
        int index = -1;

        inPath = inPath.trim();
        index = inPath.indexOf(":\\"); // nores
        inPath = inPath.replace('\\', '/');

        if (index > -1) {
            path.append("//"); // nores
            path.append(inPath.substring(0, index));
            path.append('/');
            path.append(inPath.substring(index + 2));
        } else {
            path.append(inPath);
        }

        return path.toString();
    }

    /**
     * 파일까지 포함된는경로에서 포함되어진 file seperators를 윈도우에 맞게 바꾸어줌. 또한 경로의 시작이 '\\'이면 윈도우
     * 드라이브 문자로 바꾸어줌.
     *
     * @param path
     *            파일까지포함된 경로 윈도우나 유닉스경로인지는 상관하지않음.
     * @return 유닉스 맞는 경로.
     */
    public static String toWindowsPath(String path) {
        String winPath = path;
        int index = winPath.indexOf("//"); // nores

        if (index > -1) {
            winPath = winPath.substring(0, index) + ":\\" // nores
                 +winPath.substring(index + 2);
        }

        index = winPath.indexOf(':');

        if (index == 1) {
            winPath = winPath.substring(0, 1).toUpperCase() +
                winPath.substring(1);
        }

        winPath = winPath.replace('/', '\\');

        return winPath;
    }

    /**
     * 입력된 파일명을 파싱하여 확장자를 구한다.
     * @author Administrator
     * @param filename
     * @return
     * @date   2007. 06. 01
     */
    public static String getFileExt(String filename) {

        String ext = "";

        if(filename==null || "".equals(filename)) {
            ext = "";
        } else {
            if(filename.indexOf(".")>-1) {
                ext = filename.substring(filename.lastIndexOf(".")+1, filename.length());
            }
        }

        return ext;
    }

    /**
     * 파일의 크기를 String으로 변환하여 리턴
     * @author Administrator
     * @param filename
     * @return
     * @date   2007. 06. 01
     */
    public static String getFileSize(Object obj) {

        long size = 0;
        String strSize = "0 byte";

        if(obj instanceof Number) {
            size = ((Number)obj).longValue();

            if(size>0) {
                if(size<1024) {
                    strSize = size + "byte";
                } else if(size < (1024*1000)) {
                    strSize = StringUtil.jeolsa( ((size / 1024.0)+""), 2 ) + "Kb";
                } else {
                    strSize = StringUtil.jeolsa( ((size / (1024*1000.0))+""), 2 ) + "Mb";
                }
            }
        }

        return strSize;
    }

    /**
     * 파일을 읽어서 문자열을 리턴한다.
     * @param fileName
     * @return
     * @throws Exception
     */
    public static String readFile(String fileName) throws Exception {

        StringBuffer sb = new StringBuffer();

        BufferedReader br=null;

        try {
        	InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), "UTF-8");
            br = new BufferedReader(isr);
            String data;

            while( (data=br.readLine())!=null ) {
                sb.append(data + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(br!=null) br.close();
        }

        return sb.toString();
    }
    /**
 	 * 파일명을 이름부분과 확장자 부분으로 분리
 	 * @param fileName	파일명
 	 * @return	이름, 확장자
 	 */
 	public static String[] getFileNamePart(String filePath) {
 		String[] filePart = new String[3];
 		
 		int idx = filePath.lastIndexOf("\\");
 		
 		String [] aStrPath = null; 
 		if(idx > -1)
 			aStrPath = StringUtil.StringSplit(filePath, "\\");
 		else
 			aStrPath = StringUtil.StringSplit(filePath, "/");
 		
 		String strFileName = aStrPath[aStrPath.length - 1];
 		
 		int index_path = -1;
 		index_path= filePath.lastIndexOf("\\");
 		if(index_path == -1)
 			index_path= filePath.lastIndexOf("/");
 		
 		String strFilePath = ""; 
 		if(index_path != -1)	
 			strFilePath = filePath.substring(0, index_path);
 		
         int index = strFileName.lastIndexOf(".");
         if (index != -1) {
             filePart[0] = strFileName.substring(0, index); //파일이름
             filePart[1] = strFileName.substring(index + 1);	//파일확장
             filePart[2] = strFilePath;	//파일이름앞 경로
         } else {
         	filePart[0] = strFileName; //파일이름
         	filePart[1] = ""; //파일확장
             filePart[2] = strFilePath; //파일이름앞 경로
         }
         
 		return filePart;
 	}
 	
	/**
	 * 파일이동
	 * @param imgSaveFile
	 * @param tempPath
	 * @param toPath
	 */
 	public static Boolean movefile(String imgSaveFile, String tempPath, String toPath) {
		// TODO Auto-generated method stub
		try {
			FileUtil.copy(imgSaveFile, tempPath, toPath);
			if(new File(toPath+imgSaveFile).exists()){
				if(!FileUtil.delete(new File(tempPath+imgSaveFile))){
					return false;
				}
			}else{
				return false;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return false;
		}
		return true;
		
	}

} //End of FileUtil.java
