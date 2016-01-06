package q.baselibrary.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QFile {

    public final static String FILE_EXTENSION_SEPARATOR = ".";

    /**
     * 获取文件名、大小、文件时间、文件格式等信息
     *
     * @param filePath
     * @return
     */
    public static ArrayList<String> getFileInfo(String filePath) {
        // 文件名、大小、文件时间、文件格式等信息
        ArrayList<String> data = null;
        File f = new File(filePath);

        if (f != null && f.exists()) {
            data = new ArrayList<String>(4);
            data.add(f.getName());
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                data.add(QSize.parseBytesUnit(fis.available()));
            } catch (Exception e) {
                data.add("");
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            data.add(QTime.getTime(f.lastModified(), QTime.DATE_FORMAT_DATE));
            data.add(getFileExtension(filePath));
        }
        return data;
    }

    public static boolean isImage(String name) {
        try {
            // 后缀
            String lower = name.toLowerCase();
            // 如果文件的后缀是jpeg,jpg, png, bmp, gif
            return (lower.endsWith(".jpeg") || lower.endsWith(".jpg")
                    || lower.endsWith(".png") || lower.endsWith(".bmp") || lower
                    .endsWith(".gif"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isAudio(String name) {
        try {
            // 后缀
            String lower = name.toLowerCase();
            return (lower.endsWith(".mp3") || lower.endsWith(".amr"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isVideo(String name) {
        try {
            // 后缀
            String lower = name.toLowerCase();
            return (lower.endsWith(".mp4") || lower.endsWith(".rmvb")
                    || lower.endsWith(".3gp") || lower.endsWith(".avi") || lower
                    .endsWith(".wmv"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isDocument(String name) {
        try {
            // 后缀
            String lower = name.toLowerCase();
            return (lower.endsWith(".txt") || lower.endsWith(".doc")
                    || lower.endsWith(".docx") || lower.endsWith(".xls")
                    || lower.endsWith(".xlsx") || lower.endsWith(".ppt")
                    || lower.endsWith(".pptx") || lower.endsWith(".pdf") || lower
                    .endsWith(".xml"));
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isApk(String name) {
        try {
            // 后缀
            return name.toLowerCase().endsWith(".apk") ? true : false;
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 打开文件
     *
     * @param file
     */
    public static void openFile(File file, Context context) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);// 设置intent的Action属性
        String type = getMimeType(file);// 获取文件file的MIME类型
        intent.setDataAndType(/* uri */Uri.fromFile(file), type);// 设置intent的data和Type属性
        context.startActivity(intent);// 跳转
    }

    /**
     * 根据文件后缀名获得对应的MIME类型
     *
     * @param file
     */
    private static String getMimeType(File file) {
        String defaultType = "*/*";
        String fileName = file.getName();
        // 获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0) {
            return defaultType;
        }
        /* 获取文件的后缀名 */
        String fileSuffix = fileName.substring(dotIndex, fileName.length()).toLowerCase();
        if ("".equals(fileSuffix))
            return defaultType;
        // 在MIME和文件类型的匹配表中找到对应的MIME类型
        String matchedType = fileTypeMap.get(fileSuffix);
        return matchedType == null ? defaultType : matchedType;
    }

    /**
     * 声明Map变量
     */
    private static Map<String, String> fileTypeMap = new HashMap<String, String>(72);

    /**
     * 设置文件类型数据
     */
    static {
        // key:后缀名,value:MIME类型
        fileTypeMap.put(".3gp", "video/3gpp");
        fileTypeMap.put(".apk", "application/vnd.android.package-archive");
        fileTypeMap.put(".asf", "video/x-ms-asf");
        fileTypeMap.put(".avi", "video/x-msvideo");
        fileTypeMap.put(".bin", "application/octet-stream");
        fileTypeMap.put(".bmp", "image/bmp");
        fileTypeMap.put(".c", "text/plain");
        fileTypeMap.put(".class", "application/octet-stream");
        fileTypeMap.put(".conf", "text/plain");
        fileTypeMap.put(".cpp", "text/plain");
        fileTypeMap.put(".doc", "application/msword");
        fileTypeMap.put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        fileTypeMap.put(".xls", "application/vnd.ms-excel");
        fileTypeMap.put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        fileTypeMap.put(".exe", "application/octet-stream");
        fileTypeMap.put(".gif", "image/gif");
        fileTypeMap.put(".gtar", "application/x-gtar");
        fileTypeMap.put(".gz", "application/x-gzip");
        fileTypeMap.put(".h", "text/plain");
        fileTypeMap.put(".htm", "text/html");
        fileTypeMap.put(".html", "text/html");
        fileTypeMap.put(".jar", "application/java-archive");
        fileTypeMap.put(".java", "text/plain");
        fileTypeMap.put(".jpeg", "image/jpeg");
        fileTypeMap.put(".jpg", "image/jpeg");
        fileTypeMap.put(".js", "application/x-javascript");
        fileTypeMap.put(".log", "text/plain");
        fileTypeMap.put(".m3u", "audio/x-mpegurl");
        fileTypeMap.put(".m4a", "audio/mp4a-latm");
        fileTypeMap.put(".m4b", "audio/mp4a-latm");
        fileTypeMap.put(".m4p", "audio/mp4a-latm");
        fileTypeMap.put(".m4u", "video/vnd.mpegurl");
        fileTypeMap.put(".m4v", "video/x-m4v");
        fileTypeMap.put(".mov", "video/quicktime");
        fileTypeMap.put(".mp2", "audio/x-mpeg");
        fileTypeMap.put(".mp3", "audio/x-mpeg");
        fileTypeMap.put(".mp4", "video/mp4");
        fileTypeMap.put(".mpc", "application/vnd.mpohun.certificate");
        fileTypeMap.put(".mpe", "video/mpeg");
        fileTypeMap.put(".mpeg", "video/mpeg");
        fileTypeMap.put(".mpg", "video/mpeg");
        fileTypeMap.put(".mpg4", "video/mp4");
        fileTypeMap.put(".mpga", "audio/mpeg");
        fileTypeMap.put(".msg", "application/vnd.ms-outlook");
        fileTypeMap.put(".ogg", "audio/ogg");
        fileTypeMap.put(".pdf", "application/pdf");
        fileTypeMap.put(".png", "image/png");
        fileTypeMap.put(".pps", "application/vnd.ms-powerpoint");
        fileTypeMap.put(".ppt", "application/vnd.ms-powerpoint");
        fileTypeMap.put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        fileTypeMap.put(".prop", "text/plain");
        fileTypeMap.put(".rc", "text/plain");
        fileTypeMap.put(".rmvb", "audio/x-pn-realaudio");
        fileTypeMap.put(".rtf", "application/rtf");
        fileTypeMap.put(".sh", "text/plain");
        fileTypeMap.put(".tar", "application/x-tar");
        fileTypeMap.put(".tgz", "application/x-compressed");
        fileTypeMap.put(".txt", "text/plain");
        fileTypeMap.put(".wav", "audio/x-wav");
        fileTypeMap.put(".wma", "audio/x-ms-wma");
        fileTypeMap.put(".wmv", "audio/x-ms-wmv");
        fileTypeMap.put(".wps", "application/vnd.ms-works");
        fileTypeMap.put(".xml", "text/plain");
        fileTypeMap.put(".z", "application/x-compress");
        fileTypeMap.put(".zip", "application/x-zip-compressed");
        fileTypeMap.put("", "*/*");
    }

    /**
     * 是否有足够空间来存储下载文件
     *
     * @param download_bytes
     * @return
     */
    public static boolean isMemorySizeAvailable(long download_bytes) {

        boolean isMemoryAvailable = false;
        long freeSpace = 0;

        if (QMobile.isSDCardEnable()) {
            try {
                StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                freeSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
                if (freeSpace > download_bytes) {
                    isMemoryAvailable = true;
                } else {
                    isMemoryAvailable = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                isMemoryAvailable = false;
            }
        } else {
            try {
                StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
                freeSpace = (long) stat.getAvailableBlocks() * (long) stat.getBlockSize();
                if (freeSpace > download_bytes) {
                    isMemoryAvailable = true;
                } else {
                    isMemoryAvailable = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                isMemoryAvailable = false;
            }
        }
        return isMemoryAvailable;
    }


    private QFile() {
        throw new AssertionError();
    }

    /**
     * read file
     *
     * @param filePath
     * @param charsetName The name of a supported {@link java.nio.charset.Charset </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static StringBuilder readFile(String filePath, String charsetName) {
        File file = new File(filePath);
        StringBuilder fileContent = new StringBuilder("");
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!fileContent.toString().equals("")) {
                    fileContent.append("\r\n");
                }
                fileContent.append(line);
            }
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            QIO.close(reader);
        }
    }

    /**
     * write file
     *
     * @param filePath
     * @param content
     * @param append   is append, if true, write to the end of file, else clear content of file and write into it
     * @return return false if content is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeFile(String filePath, String content, boolean append) {
        if (QString.isEmpty(content)) {
            return false;
        }

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            fileWriter.write(content);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            QIO.close(fileWriter);
        }
    }

    /**
     * write file
     *
     * @param filePath
     * @param contentList
     * @param append      is append, if true, write to the end of file, else clear content of file and write into it
     * @return return false if contentList is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeFile(String filePath, List<String> contentList, boolean append) {
        if (QList.isEmpty(contentList)) {
            return false;
        }

        FileWriter fileWriter = null;
        try {
            makeDirs(filePath);
            fileWriter = new FileWriter(filePath, append);
            int i = 0;
            for (String line : contentList) {
                if (i++ > 0) {
                    fileWriter.write("\r\n");
                }
                fileWriter.write(line);
            }
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            QIO.close(fileWriter);
        }
    }

    /**
     * write file, the string will be written to the begin of the file
     *
     * @param filePath
     * @param content
     * @return
     */
    public static boolean writeFile(String filePath, String content) {
        return writeFile(filePath, content, false);
    }

    /**
     * write file, the string list will be written to the begin of the file
     *
     * @param filePath
     * @param contentList
     * @return
     */
    public static boolean writeFile(String filePath, List<String> contentList) {
        return writeFile(filePath, contentList, false);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @param filePath
     * @param stream
     * @return
     * @see {@link #writeFile(String, InputStream, boolean)}
     */
    public static boolean writeFile(String filePath, InputStream stream) {
        return writeFile(filePath, stream, false);
    }

    /**
     * write file
     *
     * @param filePath the file to be opened for writing.
     * @param stream   the input stream
     * @param append   if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean writeFile(String filePath, InputStream stream, boolean append) {
        return writeFile(filePath != null ? new File(filePath) : null, stream, append);
    }

    /**
     * write file, the bytes will be written to the begin of the file
     *
     * @param file
     * @param stream
     * @return
     * @see {@link #writeFile(File, InputStream, boolean)}
     */
    public static boolean writeFile(File file, InputStream stream) {
        return writeFile(file, stream, false);
    }

    /**
     * write file
     *
     * @param file   the file to be opened for writing.
     * @param stream the input stream
     * @param append if <code>true</code>, then bytes will be written to the end of the file rather than the beginning
     * @return return true
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean writeFile(File file, InputStream stream, boolean append) {
        OutputStream o = null;
        try {
            makeDirs(file.getAbsolutePath());
            o = new FileOutputStream(file, append);
            byte data[] = new byte[1024];
            int length = -1;
            while ((length = stream.read(data)) != -1) {
                o.write(data, 0, length);
            }
            o.flush();
            return true;
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            QIO.close(o);
            QIO.close(stream);
        }
    }

    /**
     * move file
     *
     * @param sourceFilePath
     * @param destFilePath
     */
    public static void moveFile(String sourceFilePath, String destFilePath) {
        if (TextUtils.isEmpty(sourceFilePath) || TextUtils.isEmpty(destFilePath)) {
            throw new RuntimeException("Both sourceFilePath and destFilePath cannot be null.");
        }
        moveFile(new File(sourceFilePath), new File(destFilePath));
    }

    /**
     * move file
     *
     * @param srcFile
     * @param destFile
     */
    public static void moveFile(File srcFile, File destFile) {
        boolean rename = srcFile.renameTo(destFile);
        if (!rename) {
            copyFile(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
            deleteFile(srcFile.getAbsolutePath());
        }
    }

    /**
     * copy file
     *
     * @param sourceFilePath
     * @param destFilePath
     * @return
     * @throws RuntimeException if an error occurs while operator FileOutputStream
     */
    public static boolean copyFile(String sourceFilePath, String destFilePath) {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sourceFilePath);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("FileNotFoundException occurred. ", e);
        }
        return writeFile(destFilePath, inputStream);
    }

    /**
     * read file to string list, a element of list is a line
     *
     * @param filePath
     * @param charsetName The name of a supported {@link java.nio.charset.Charset </code>charset<code>}
     * @return if file not exist, return null, else return content of file
     * @throws RuntimeException if an error occurs while operator BufferedReader
     */
    public static List<String> readFileToList(String filePath, String charsetName) {
        File file = new File(filePath);
        List<String> fileContent = new ArrayList<String>();
        if (file == null || !file.isFile()) {
            return null;
        }

        BufferedReader reader = null;
        try {
            InputStreamReader is = new InputStreamReader(new FileInputStream(file), charsetName);
            reader = new BufferedReader(is);
            String line = null;
            while ((line = reader.readLine()) != null) {
                fileContent.add(line);
            }
            return fileContent;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            QIO.close(reader);
        }
    }

    /**
     * get file name from path, not include suffix
     * <p/>
     * <pre>
     *      getFileNameWithoutExtension(null)               =   null
     *      getFileNameWithoutExtension("")                 =   ""
     *      getFileNameWithoutExtension("   ")              =   "   "
     *      getFileNameWithoutExtension("abc")              =   "abc"
     *      getFileNameWithoutExtension("a.mp3")            =   "a"
     *      getFileNameWithoutExtension("a.b.rmvb")         =   "a.b"
     *      getFileNameWithoutExtension("c:\\")              =   ""
     *      getFileNameWithoutExtension("c:\\a")             =   "a"
     *      getFileNameWithoutExtension("c:\\a.b")           =   "a"
     *      getFileNameWithoutExtension("c:a.txt\\a")        =   "a"
     *      getFileNameWithoutExtension("/home/admin")      =   "admin"
     *      getFileNameWithoutExtension("/home/admin/a.txt/b.mp3")  =   "b"
     * </pre>
     *
     * @param filePath
     * @return file name from path, not include suffix
     * @see
     */
    public static String getFileNameWithoutExtension(String filePath) {
        if (QString.isEmpty(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (filePosi == -1) {
            return (extenPosi == -1 ? filePath : filePath.substring(0, extenPosi));
        }
        if (extenPosi == -1) {
            return filePath.substring(filePosi + 1);
        }
        return (filePosi < extenPosi ? filePath.substring(filePosi + 1, extenPosi) : filePath.substring(filePosi + 1));
    }

    /**
     * get file name from path, include suffix
     * <p/>
     * <pre>
     *      getFileName(null)               =   null
     *      getFileName("")                 =   ""
     *      getFileName("   ")              =   "   "
     *      getFileName("a.mp3")            =   "a.mp3"
     *      getFileName("a.b.rmvb")         =   "a.b.rmvb"
     *      getFileName("abc")              =   "abc"
     *      getFileName("c:\\")              =   ""
     *      getFileName("c:\\a")             =   "a"
     *      getFileName("c:\\a.b")           =   "a.b"
     *      getFileName("c:a.txt\\a")        =   "a"
     *      getFileName("/home/admin")      =   "admin"
     *      getFileName("/home/admin/a.txt/b.mp3")  =   "b.mp3"
     * </pre>
     *
     * @param filePath
     * @return file name from path, include suffix
     */
    public static String getFileName(String filePath) {
        if (QString.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? filePath : filePath.substring(filePosi + 1);
    }

    /**
     * get folder name from path
     * <p/>
     * <pre>
     *      getFolderName(null)               =   null
     *      getFolderName("")                 =   ""
     *      getFolderName("   ")              =   ""
     *      getFolderName("a.mp3")            =   ""
     *      getFolderName("a.b.rmvb")         =   ""
     *      getFolderName("abc")              =   ""
     *      getFolderName("c:\\")              =   "c:"
     *      getFolderName("c:\\a")             =   "c:"
     *      getFolderName("c:\\a.b")           =   "c:"
     *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
     *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
     *      getFolderName("/home/admin")      =   "/home"
     *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
     * </pre>
     *
     * @param filePath
     * @return
     */
    public static String getFolderName(String filePath) {

        if (QString.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }

    /**
     * get suffix of file from path
     * <p/>
     * <pre>
     *      getFileExtension(null)               =   ""
     *      getFileExtension("")                 =   ""
     *      getFileExtension("   ")              =   "   "
     *      getFileExtension("a.mp3")            =   "mp3"
     *      getFileExtension("a.b.rmvb")         =   "rmvb"
     *      getFileExtension("abc")              =   ""
     *      getFileExtension("c:\\")              =   ""
     *      getFileExtension("c:\\a")             =   ""
     *      getFileExtension("c:\\a.b")           =   "b"
     *      getFileExtension("c:a.txt\\a")        =   ""
     *      getFileExtension("/home/admin")      =   ""
     *      getFileExtension("/home/admin/a.txt/b")  =   ""
     *      getFileExtension("/home/admin/a.txt/b.mp3")  =   "mp3"
     * </pre>
     *
     * @param filePath
     * @return
     */
    public static String getFileExtension(String filePath) {
        if (QString.isBlank(filePath)) {
            return filePath;
        }

        int extenPosi = filePath.lastIndexOf(FILE_EXTENSION_SEPARATOR);
        int filePosi = filePath.lastIndexOf(File.separator);
        if (extenPosi == -1) {
            return "";
        }
        return (filePosi >= extenPosi) ? "" : filePath.substring(extenPosi + 1);
    }

    /**
     * Creates the directory named by the trailing filename of this file, including the complete directory path required
     * to create this directory. <br/>
     * <br/>
     * <ul>
     * <strong>Attentions:</strong>
     * <li>makeDirs("C:\\Users\\Trinea") can only create users folder</li>
     * <li>makeFolder("C:\\Users\\Trinea\\") can create Trinea folder</li>
     * </ul>
     *
     * @param filePath
     * @return true if the necessary directories have been created or the target directory already exists, false one of
     * the directories can not be created.
     * <ul>
     * <li>if {@link QFile#getFolderName(String)} return null, return false</li>
     * <li>if target directory already exists, return true</li>
     * <li>return {@link java.io.File#makeFolder}</li>
     * </ul>
     */
    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (QString.isEmpty(folderName)) {
            return false;
        }

        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }

    /**
     * @param filePath
     * @return
     * @see #makeDirs(String)
     */
    public static boolean makeFolders(String filePath) {
        return makeDirs(filePath);
    }

    /**
     * Indicates if this file represents a file on the underlying file system.
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        if (QString.isBlank(filePath)) {
            return false;
        }

        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * Indicates if this file represents a directory on the underlying file system.
     *
     * @param directoryPath
     * @return
     */
    public static boolean isFolderExist(String directoryPath) {
        if (QString.isBlank(directoryPath)) {
            return false;
        }

        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    /**
     * delete file or directory
     * <ul>
     * <li>if path is null or empty, return true</li>
     * <li>if path not exist, return true</li>
     * <li>if path exist, delete recursion. return true</li>
     * <ul>
     *
     * @param path
     * @return
     */
    public static boolean deleteFile(String path) {
        if (QString.isBlank(path)) {
            return true;
        }

        File file = new File(path);
        if (!file.exists()) {
            return true;
        }
        if (file.isFile()) {
            return file.delete();
        }
        if (!file.isDirectory()) {
            return false;
        }
        for (File f : file.listFiles()) {
            if (f.isFile()) {
                f.delete();
            } else if (f.isDirectory()) {
                deleteFile(f.getAbsolutePath());
            }
        }
        return file.delete();
    }

    /**
     * get file size
     * <ul>
     * <li>if path is null or empty, return -1</li>
     * <li>if path exist and it is a file, return file size, else return -1</li>
     * <ul>
     *
     * @param path
     * @return returns the length of this file in bytes. returns -1 if the file does not exist.
     */
    public static long getFileSize(String path) {
        if (QString.isBlank(path)) {
            return -1;
        }

        File file = new File(path);
        return (file.exists() && file.isFile() ? file.length() : -1);
    }
}
