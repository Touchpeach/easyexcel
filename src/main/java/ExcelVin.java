import cn.hutool.Hutool;
import cn.hutool.core.date.BetweenFormater;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.model.ChargingTime;
import com.alibaba.excel.model.VinReadModel;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.util.JsonUtilO;
import com.alibaba.excel.util.RegUtil;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.sql.SQLOutput;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * create pengtao
 **/
public class ExcelVin {

    public static void main(String[] args) {

        List<VinReadModel> vinReadModels = new ArrayList<VinReadModel>();
        //遍历文件夹
        File file = new File("c:/excel/02-11");

        for (File listFile : file.listFiles()) {

            File excelFile = new File(listFile.toString());

            for (File file1 : excelFile.listFiles()) {
                System.out.println("vin号" + excelFile.getName() + "   文件的时间" + file1.getName().substring(8,18));

                VinReadModel date = getDate(file1.toString());
                date.setVin(excelFile.getName());
                date.setFileTime(file1.getName().substring(8,18));
                vinReadModels.add(date);
            }
        }

        writeData(vinReadModels);


    }

    /**
     * 从文件中获取数据
     * @param fileName
     * @return
     */
    public static VinReadModel getDate(String fileName) {

        VinReadModel vinReadModel = new VinReadModel();

        //fileName = "c:/excel/02-11/LS4ASE2C0JF013270/history_2018-11-30.xls";

        //读取文件内容
        File file = new File(fileName);

        BufferedInputStream bufferedInputStream = null;
        try {
            bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //导入数据到data
        List<Object> data = EasyExcelFactory.read(bufferedInputStream, new Sheet(1, 1));
        List<ChargingTime> chargingTimes = new ArrayList<ChargingTime>();

        for(int i = 0 ; i < data.size() ; i++){
            List<String> str = (List<String>) data.get(i);
            List<String> strlast = null;


            if(i == data.size()-1){
                strlast = (List<String>) data.get(i);
            } else{
                strlast = (List<String>) data.get(i + 1);
            }

            //保存记录的时间

            ChargingTime chargingTime = new ChargingTime();
            //最后一个里程的数作为总里程
            if(i == data.size()-1){
                //4是里程数,8是停车状态,0是时间,1是服务器接收时间
                vinReadModel.setMileage(str.get(4));
            }

            //如果第一条数据就是充电状态
            if(i == 0){
                if(str.get(8).equals("停车充电")){
                    //todo 存点的起点
                    chargingTime.setStartTime(str.get(0));
                }
            }
            if((str.get(8).equals("未充电状态") && strlast.get(8).equals("停车充电")) || (str.get(8).equals("充电完成") && strlast.get(8).equals("停车充电"))){
                //todo 保存存电的起点
                chargingTime.setStartTime(strlast.get(0));
            }


            if((str.get(8).equals("停车充电") && strlast.get(8).equals("未充电状态")) || (str.get(8).equals("停车充电") && strlast.get(8).equals("异常"))){
                //todo 结束存电
                chargingTime.setEndTime(str.get(0));
            }

            if(i == data.size()-1){
                if(str.get(8).equals("停车充电")){
                    //todo 结束充电状态
                    chargingTime.setEndTime(str.get(0));
                }
            }

            chargingTimes.add(chargingTime);

        }

        try {
            bufferedInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        vinReadModel.setChargingTime(chargingTimes);
        return vinReadModel;

    }

    /**
     * 写数据到excel
     * @param date1
     */
    public static void writeData(List<VinReadModel> date1){
        OutputStream out = null;
        try {
            out = new FileOutputStream("c:/excel/export.xls");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ExcelWriter writer = EasyExcelFactory.getWriter(out, ExcelTypeEnum.XLS,true);
        //写第一个sheet, sheet1  数据全是List<String> 无模型映射关系
        Sheet sheet1 = new Sheet(1, 3);
        sheet1.setSheetName("第一个sheet");

        //设置列宽 设置每列的宽度
//        Map columnWidth = new HashMap();
//        columnWidth.put(0,1000);columnWidth.put(1,1000);columnWidth.put(2,1000);columnWidth.put(3,1000);
//        sheet1.setColumnWidthMap(columnWidth);

        writer.write1(createTestListObject(date1), sheet1);

        writer.finish();
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * excel表的保存字段
     * @param date1
     * @return
     */
    public static List<List<Object>> createTestListObject(List<VinReadModel> date1) {
        List<List<Object>> object = new ArrayList<List<Object>>();
        for (int i = 0; i < date1.size(); i++) {
            List<Object> da = new ArrayList<Object>();
            da.add(date1.get(i).getVin());
            da.add(date1.get(i).getMileage());
            da.add(date1.get(i).getFileTime());
            System.out.println("开始计算");
            //计算时长
            Object o = null;
            List<ChargingTime> chargingTime = date1.get(i).getChargingTime();
            try {
                o = JsonUtilO.filterNone(JSON.toJSONString(chargingTime));
            } catch (Exception e) {
                e.printStackTrace();
            }

            ChargingTime chargingTime1 = new ChargingTime();
            Long Ltime = 1L;
            Long timeZone2 = 1L;

            if(o != null){
                System.out.println(o.toString());

                List<Map<String,Object>> list = (List<Map<String, Object>>) o;

                if(list != null){
                    for (int i1 = 0; i1 < list.size() ; i1 ++) {

                        if(i1%2 == 1){
                            chargingTime1.setEndTime(list.get(i1).values().toString());
                            timeZone2 = getTimeZone2(chargingTime1);
                            Ltime = timeZone2 + Ltime;
                        }else{
                            chargingTime1.setStartTime(list.get(i1).values().toString());
                        }
                    }
                }


            }

            //Level.MINUTE表示精确到分
            String s = DateUtil.formatBetween(Ltime, BetweenFormater.Level.MINUTE);
            da.add(s);

            da.add(o);
            object.add(da);

        }
        return object;
    }

    /**
     * 获取时间的区域
     * @return
     * @param chargingTime
     */
    public static String getTimeZone(List<ChargingTime> chargingTime) {

        Long Ltime = 1L;

        String formatBetween = "";
        if (chargingTime != null) {

            for (ChargingTime time : chargingTime) {
                if(time != null && RegUtil.isNotNull(time.getEndTime()) && RegUtil.isNotNull(time.getEndTime())){
                    System.out.println(time.getStartTime());
                    System.out.println(time.getEndTime());
                    long l = DateUtil.betweenMs(DateUtil.parse(StringUtils.strip(time.getStartTime(), "[]")),DateUtil.parse(StringUtils.strip(time.getEndTime(), "[]")));
                    Ltime = Ltime + l;
                }
            }

            //Level.MINUTE表示精确到分
            formatBetween = DateUtil.formatBetween(Ltime, BetweenFormater.Level.MINUTE);
            //输出：31天1小时
            System.out.println(formatBetween);
        }


        System.out.println(Ltime.toString());
        return formatBetween;
    }

    public static Long getTimeZone2(ChargingTime time){

        Long Ltime = 1L;
        long l = DateUtil.betweenMs(DateUtil.parse(StringUtils.strip(time.getStartTime(), "[]")),DateUtil.parse(StringUtils.strip(time.getEndTime(), "[]")));
        Ltime = Ltime + l;

        return Ltime;
    }



}
