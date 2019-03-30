import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.model.ReadModel;
import com.alibaba.excel.util.FileUtil;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;

import static jdk.nashorn.internal.objects.Global.print;

/**
 * create pengtao
 **/
public class ExcelVin {

    public static void main(String[] args) throws FileNotFoundException, IllegalAccessException {

        File file = new File("c:/excel/02-11");

        for (File listFile : file.listFiles()) {

            File excelFile = new File(listFile.toString());
            //System.out.println("获取的vin号：" + excelFile.getName());
            for (File file1 : excelFile.listFiles()) {
                System.out.println("文件的时间" + file1.getName().substring(8,18) + "_vin号" + excelFile.getName());
            }
        }
        //读取文件内容
//        File file = new File("c:/excel/2003.xls");
//        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
//
//        List<Object> data = EasyExcelFactory.read(bufferedInputStream, new Sheet(2, 1,ReadModel.class));
//        System.out.println(data);
//        for (Object datum : data) {
//            System.out.println(datum);
//        }
//        try {
//            bufferedInputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

}
