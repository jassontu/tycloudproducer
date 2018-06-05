 package ty.cloud.netty.mq.server.utils;
 
 import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
 public class ClassUtils
 {
   public static Logger logger = LoggerFactory.getLogger(ClassUtils.class);
 
   public static Object getBean(String className)
   {
     Class clazz = null;
     try
     {
       clazz = Class.forName(className);
     }
     catch (Exception ex)
     {
       logger.info(""+ex.getMessage());
     }
     if (clazz != null)
     {
       try
       {
         return clazz.newInstance();
       }
       catch (Exception ex) {
         logger.info(""+ex.getMessage());
       }
     }
     return null;
   }
 }

