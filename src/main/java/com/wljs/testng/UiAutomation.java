package com.wljs.testng;

import com.wljs.pojo.StfDevicesFields;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 创建testng.xml
 */
public class UiAutomation {

    public void createTestngXml(List<StfDevicesFields> fieldsList) {
        try {
            List<String> list = new ArrayList<String>();
            for (StfDevicesFields fields : fieldsList) {

                //版本型号相同的设备，不用重复跑自动化
                String key = fields.getDeviceName() + "::" + fields.getVersion();

                if (list.contains(key)) {
                    continue;
                }
                list.add(key);

                //记录要保存的xml文件位置  
                String xmlfilepath = "/Volumes/SoureCode/testng_" + fields.getSerial() + ".xml";

                //创建Document实例  
                Document document = DocumentHelper.createDocument();
                document.addDocType("suite", null, "http://testng.org/testng-1.0.dtd");

                //根节点
                Element suite = document.addElement("suite").addAttribute("name", "executeTestCase");

                //子节点
                Element test = suite.addElement("test").addAttribute("name", "test");

                //参数
                Element manufacturerParams = test.addElement("parameter");
                manufacturerParams.addAttribute("name", "manufacturer");
                manufacturerParams.addAttribute("value", fields.getManufacturer());

                Element modelParams = test.addElement("parameter");
                modelParams.addAttribute("name", "model");
                modelParams.addAttribute("value", fields.getModel());

                Element serialParams = test.addElement("parameter");
                serialParams.addAttribute("name", "serial");
                serialParams.addAttribute("value", fields.getSerial());

                Element versionParams = test.addElement("parameter");
                versionParams.addAttribute("name", "version");
                versionParams.addAttribute("value", fields.getVersion());

                Element deviceNameParams = test.addElement("parameter");
                deviceNameParams.addAttribute("name", "deviceName");
                deviceNameParams.addAttribute("value", fields.getDeviceName());

                Element platformParams = test.addElement("parameter");
                platformParams.addAttribute("name", "platform");
                platformParams.addAttribute("value", fields.getPlatform());

                Element appiumServerPortParams = test.addElement("parameter");
                appiumServerPortParams.addAttribute("name", "appiumServerPort");
                appiumServerPortParams.addAttribute("value", String.valueOf(fields.getAppiumServerPort()));

                Element systemPortParams = test.addElement("parameter");
                systemPortParams.addAttribute("name", "systemPort");
                systemPortParams.addAttribute("value", String.valueOf(fields.getSystemPort()));


                Element wdaLocalPortParams = test.addElement("parameter");
                wdaLocalPortParams.addAttribute("name", "wdaLocalPort");
                wdaLocalPortParams.addAttribute("value", String.valueOf(fields.getWdaLocalPort()));

                Element indexParams = test.addElement("parameter");
                indexParams.addAttribute("name", "index");
                indexParams.addAttribute("value", String.valueOf(fields.getIndex()));

                //classes
                Element classes = test.addElement("classes");


                Element classEm = classes.addElement("class");
                classEm.addAttribute("name", "com.wljs.testcase.RegressionTesting");


                //listeners
                Element listeners = suite.addElement("listeners");

                Element listener = listeners.addElement("listener").addAttribute("class-name", "com.wljs.testcase.testng.TestngListener");


                //输出格式设置  
                OutputFormat format = OutputFormat.createPrettyPrint();
                format = OutputFormat.createCompactFormat();

                //设置输出编码  
                format.setEncoding("UTF-8");

                //创建XML文件
                XMLWriter writer = new XMLWriter(new OutputStreamWriter(new FileOutputStream(xmlfilepath), format.getEncoding()), format);
                writer.write(document);
                writer.close();
                document = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
