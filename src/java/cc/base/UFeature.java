package cc.base;

import java.sql.*;

public class UFeature {
    public int rootParentRID = 0;
    public String rootParentDesc = "";
    public String featName = "";
    public String featCommand = "";
    public String featHelp = "";
    public String featGroup = "";
    public String featGroupDesc = "";
    public String featCode = "";
    public int featParentGroup = 0;
    public int offlineSupported = 0;
    public int featRID = 0 ;
    public int featureRID_L2 = 0; // for level 2 feature items
    public String featureName_L2 = ""; // hack need to do in better way
    public String featureCommand_L2 = "";
    public String featureCode_L2 = "";
    public String featureGroup_L2 = "";
    public int isFeatureUnitSpecific = 0;
            
    
    public UFeature(String name, String command, String help, String group, String groupDesc, int featureRID) {
        
        featName = name;
        featCommand = command;
        featHelp = help;
        featGroup = group ;
        featGroupDesc = groupDesc ;
        featRID = featureRID ;
    }
    
    public UFeature(String name, String command, String help, String group, String groupDesc, int featureRID, int featParentGroup_1) {
        
        featName = name;
        featCommand = command;
        featHelp = help;
        featGroup = group ;
        featGroupDesc = groupDesc ;
        featParentGroup = featParentGroup_1;
        featRID = featureRID ;
    }
    
    public UFeature(String name, String command, String help, String group, String groupDesc,
            int featureRID, int featParentGroup, int rootParentRID, String rootParentDesc, String featureCode, int offlineSupported, int isFeatureUnitSpecific) {
        
        featName = name;
        featCommand = command;
        featHelp = help;
        featGroup = group ;
        featGroupDesc = groupDesc ;
        this.featParentGroup = featParentGroup;
        featRID = featureRID ;
        this.rootParentRID = rootParentRID;
        this.rootParentDesc = rootParentDesc;
        featCode = featureCode ;
        this.offlineSupported = offlineSupported;
        this.isFeatureUnitSpecific = isFeatureUnitSpecific;
    }
    
    public UFeature(String name, String command, String help, String group, String groupDesc,
            int featureRID, int featParentGroup, int rootParentRID, String rootParentDesc, 
            String featureCode, int offlineSupported, int featureRID_L2, String featureName_L2, 
            String featureCommand_L2, String featureCode_L2, String featureGroup_L2, int isFeatureUnitSpecific) {
        
        featName = name;
        featCommand = command;
        featHelp = help;
        featGroup = (group != null && !"".equals(group))? group.trim() : group ;
        featGroupDesc = groupDesc ;
        this.featParentGroup = featParentGroup;
        featRID = featureRID ;
        this.rootParentRID = rootParentRID;
        this.rootParentDesc = rootParentDesc;
        featCode = featureCode ;
        this.offlineSupported = offlineSupported;
        this.featureRID_L2 = featureRID_L2;
        this.featureName_L2 = featureName_L2;
        this.featureCommand_L2 = featureCommand_L2;
        this.featureCode_L2 = featureCode_L2;
        this.featureGroup_L2 = featureGroup_L2;
        this.isFeatureUnitSpecific = isFeatureUnitSpecific;
    }
 
    public static ResultSet getFeature(URequestContext ctxt, int productRID, String featureCode)
    throws UDBAccessException {
        
        String sql = "select * from u_feature where feature_code = '" + featureCode + "' and feature_valid = 1";
        
        UQueryEngine qe = ctxt.getQueryEngine();
        
        ResultSet rs = qe.executeQuery(sql);
        
        return rs;
    }
    
    public static int getFeatureRID(URequestContext ctxt, int productRID, String featureCode)
    throws UDBAccessException, SQLException {
        
        String sql = "select * from u_feature where feature_code = '" + featureCode + "' and feature_valid = 1";
        
        UQueryEngine qe = ctxt.getQueryEngine();
        
        ResultSet rs = qe.executeQuery(sql);
        
        if(rs.next())
            return rs.getInt("feature_rid");
        else
            throw new UDBAccessException("Feature code '" + featureCode + "' undefined. Please contact the system adminstrator", null);
    }
    
    public static int addAutoGeneratedFeature(URequestContext ctxt, int productRID,
            String featureCode, String featureName)
            throws UDBAccessException {
        
        // Check if a feature with this code exists
        
        String sql = "select feature_rid from u_feature where feature_code = '" + featureCode + "' and feature_valid = 1";
        
        UQueryEngine qe = ctxt.getQueryEngine();
        
        ResultSet rs = qe.executeQuery(sql);
        
        try {
            if(rs.next()) {
                return rs.getInt("feature_rid");
            }
        } catch (Exception e) {
            throw new UDBAccessException(e.getMessage(), e);
        }
        
        // None found. Create a new one.
        
        sql = "insert into u_feature (feature_prod_rid, feature_code, feature_name, " +
                "feature_command, feature_group, feature_group_desc, feature_seq_num, " +
                "feature_valid) values (" +
                productRID + ", '" + featureCode + "', '" + featureName + "', '', 0, '', 1, 1)";
        
        return qe.executeInsert(sql);
    }
    public static void updateAutoGeneratedFeature(URequestContext ctxt, int productRID,
    String featureCode, String featureName)
        throws UDBAccessException {
        
        UQueryEngine qe = ctxt.getQueryEngine();
        String sql = " Update u_feature set feature_name = '" + featureName + "' where feature_code like '" + featureCode + "'" +
                " and feature_prod_rid = " + productRID + " and feature_valid = 1 " ;
        qe.executeUpdate(sql);                        
    }
    
    public static boolean isFeatureUnitSpecific(URequestContext ctxt, int featureRID)
    throws UDBAccessException, SQLException {
        
        boolean isFeatureUnitSpecific = false;
        
        UQueryEngine qe = ctxt.getQueryEngine();
       
        String sql = " select feature_is_unit_specific from u_feature where feature_rid = " + featureRID;
        ResultSet rs = qe.executeQuery(sql);
        
        if(rs != null && rs.first()){
          isFeatureUnitSpecific = rs.getInt("feature_is_unit_specific")== 1 ?  true : false; 
        }
       
        return isFeatureUnitSpecific;
    }
    
    public static boolean checkForUnitRestrictedFeature(URequestContext ctxt, int featureRID)
    throws UDBAccessException, SQLException {
        
         boolean  featureIsUnitSpecific  = false;
         UQueryEngine qe = ctxt.getQueryEngine();
         String unitRIDs = "";
         String sql = " select *  from u_feature_unit_map " +
                 " where fum_feature_rid = " + featureRID ;
         ResultSet rs = qe.executeQuery(sql);
         
         if(rs != null && rs.first()){
            rs.beforeFirst();
            while(rs.next()){
                if(rs.getInt("fum_unit_rid") == ctxt.getUserUnitRID()){
                     featureIsUnitSpecific = true;
                     break;
                }
            }
         }
      return  featureIsUnitSpecific;  
    }
    

}