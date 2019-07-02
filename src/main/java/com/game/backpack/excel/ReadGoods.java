package com.game.backpack.excel;

import com.alibaba.fastjson.JSONObject;
import com.game.annotation.ExcelAnnotation;
import com.game.backpack.bean.Goods;
import com.game.property.bean.PropertyType;
import com.game.utils.MapUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 读取Excel文件
 * @author lmb
 * @date 2017-3-15
 *
 */
@ExcelAnnotation
@Component
public class ReadGoods {
	private static final String FILEPATH = "src/main/resources/resource/goods.xls";

    /**
     * 读取excel
     * @return
     */
	@ExcelAnnotation
	public static void readFromXLSX2007() {
         //Excel文件对象
        File excelFile = null;
         //输入流对象
        InputStream is = null;
         //单元格，最终按字符串处理
        String cellStr = null;
        try {
            // 获取文件输入流
            excelFile = new File(FILEPATH);
            is = new FileInputStream(excelFile);
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);
            // 开始循环遍历行，表头不处理，从1开始
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                // 实例化对象
                Goods goods = new Goods();
                // 获取行对象
            	Row row = sheet.getRow(i);
                // 如果为空，不处理
                if (row == null) {
                    continue;  
                }  
                // 循环遍历单元格(每一列)
                for (int j = 0; j < row.getLastCellNum(); j++) {
                    // 获取单元格对象
                	Cell cell = row.getCell(j);
                    // 单元格为空设置cellStr为空串
                    cellStr =  getResult(cell);
                    // 下面按照数据出现位置封装到bean中
                    if(j == 0) {
                        goods.setId(new Double(cellStr).intValue());
                    }else if (j == 1) {
                        goods.setName(cellStr);
                    }else if (j == 2) {
                        goods.setType(new Double(cellStr).intValue());
                    }else if (j == 3) {
                        goods.setDescription(cellStr);
                    }else if (j == 4) {
                        goods.setCount(new Double(cellStr).intValue());
                    }else if (j == 5) {
                        goods.setRepeat(new Double(cellStr).intValue());
                    }else if(j == 6){
                        goods.setProperty(JSONObject.parseObject(cellStr));
                    }else if(j == 7){
                        goods.setDurability(new Double(cellStr).intValue());
                    }else if(j == 8){
                        goods.setCost(new Double(cellStr).intValue());
                    }
                }
                // 数据装入List
                MapUtils.getGoodsMap().put(goods.getName(),goods);
            }
            //获取goodsMap
            Map<String, Goods> goodsMap = MapUtils.getGoodsMap();
            //获取goodsMap的entries
            Set<Map.Entry<String, Goods>> entries = goodsMap.entrySet();
            //获取迭代器
            Iterator<Map.Entry<String, Goods>> iterator = entries.iterator();

            //创建一个新List<Goods>
            List<Goods> goodsList = MapUtils.getGoodsList();
            //遍历
            while (iterator.hasNext()) {
                Map.Entry<String, Goods> next = iterator.next();
                Map<PropertyType,Integer> map = new HashMap<>();
                //获取Goods实例
                Goods goods = next.getValue();
                goods.setPropertyMap(map);
                //获取每个goods对象的property属性（json类型）
                JSONObject jsonObject = goods.getProperty();
                Set<Map.Entry<String, Object>> set = jsonObject.entrySet();
                Iterator<Map.Entry<String, Object>> entryIterator = set.iterator();
                while (entryIterator.hasNext()) {
                    Map.Entry<String, Object> next1 = entryIterator.next();
                    String key = next1.getKey();
                    PropertyType propertyType = PropertyType.map.get(key);
                    String val = jsonObject.getString(key);
                    goods.getPropertyMap().put(propertyType,Integer.parseInt(val));
                }
                goodsList.add(goods);
            }


            System.out.println("Goods静态数据加载完毕");
        } catch (IOException e) {
            e.printStackTrace();
        }  catch (org.apache.poi.openxml4j.exceptions.InvalidFormatException e) {
            e.printStackTrace();
        } finally {
            // 关闭文件流
            if (is != null) {  
                try {  
                    is.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }

    private static String getResult(Cell cell) {
	    String cellStr = null;
        if (cell == null) {
            cellStr = "";
        }else {
            switch (cell.getCellTypeEnum()){
                case NUMERIC:
                    cellStr = cell.getNumericCellValue() + "";
                    break;
                case BOOLEAN:
                    cellStr = String.valueOf(cell.getBooleanCellValue());
                    break;
                default: cellStr = cell.getStringCellValue();
            }
        }
        return cellStr;
    }

    public static void main(String[] args) {
        System.out.println(MapUtils.getListRole().size());
    }
}  