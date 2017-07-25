package de.edittrich.dynamodb;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Application
{
    public static void main(String[] args)
    {
        String tableName = "Sample";
        
        String itemAttribute = "Name";
        String itemValue = "Test1";
        String projectionExpression = "#n, #u, Types";
        
        String queryAttribute = "Types";
        String queryValue = "Type2";
        
        String mapperAttribute = "Types";
        String mapperValue = "Type2";

        final AmazonDynamoDB ddb = AmazonDynamoDBClientBuilder.standard()
        	.withRegion(Regions.US_EAST_1)
        	.withCredentials(new DefaultAWSCredentialsProviderChain())
        	.build();

        //
                
        System.out.println("getItem");

        HashMap<String, AttributeValue> key_to_get
        	= new HashMap<String, AttributeValue>();
        key_to_get.put(itemAttribute, new AttributeValue(itemValue));
        
        HashMap<String, String> expressionAttribute
    		= new HashMap<String, String>();
        expressionAttribute.put("#n", "Name");
        expressionAttribute.put("#u", "Number");

        GetItemRequest request = new GetItemRequest()
        	.withKey(key_to_get)
        	.withTableName(tableName)
        	.withExpressionAttributeNames(expressionAttribute)
        	.withProjectionExpression(projectionExpression);
        
        try {
            Map<String,AttributeValue> returned_item = ddb.getItem(request).getItem();
            if (returned_item != null) {
                Set<String> keys = returned_item.keySet();
                for (String key : keys) {
                    System.out.format("%s: %s\n",
                		key, returned_item.get(key).toString());
                }
            } else {
                System.out.format("No item found with the key %s!\n",
                	itemValue);
            }
        } catch (AmazonServiceException e) {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        
        //
        
        System.out.println("\nScan (doesn't work with CONTAIN)");
        HashMap<String, Condition> scanFilter
        	= new HashMap<String, Condition>();
        Condition condition = new Condition()
            .withComparisonOperator(ComparisonOperator.EQ.toString())
            .withAttributeValueList(new AttributeValue().withSS(queryValue));
        scanFilter.put(queryAttribute, condition);
        ScanRequest scanRequest = new ScanRequest(tableName)
        	.withScanFilter(scanFilter);
        ScanResult scanResult = ddb.scan(scanRequest);
        System.out.println("Result: " + scanResult);
        
        //
        
        System.out.println("\nScan with Mapper");
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);
        
        Map<String, AttributeValue> eav = new HashMap<String, AttributeValue>();
        eav.put(":val1", new AttributeValue().withS(mapperValue));
  
        DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
        		.withFilterExpression("contains(" + mapperAttribute +", :val1)")
        		.withExpressionAttributeValues(eav);

        List<SampleItem> scanResultMapper = mapper.scan(SampleItem.class, scanExpression);

        for (SampleItem sampleItem : scanResultMapper) {
            System.out.println(sampleItem);
            System.out.println("Number: " + sampleItem.getNumber());
            
            int count = 0;
            Iterator<String> it = sampleItem.getTypes().iterator();
            while (it.hasNext())
            {
            	count++;
            	it.next();
            }
            
            int i = 0;
            String setText = "Types: ";
            it = sampleItem.getTypes().iterator();
            while (it.hasNext())
            {
            	i++;
            	setText = setText + (String) it.next();
            	if (i == count-1) {
                	setText = setText + " and ";            		
            	}
            	if (i < count-1) {
            		setText = setText + ", ";            		
            	}
            }
            
            System.out.println(setText + ".");
        }
      
        //

        SampleItem sampleItem = new SampleItem();
        sampleItem.setName("Test1");
        sampleItem.setNumber(1);
        Set<String> types = new TreeSet<String>();
        types.add("Type1");
        types.add("Type2");
        types.add("Type3");
        mapper = new DynamoDBMapper(ddb);
        sampleItem.setTypes(types);

        mapper.save(sampleItem);
        sampleItem = new SampleItem();
        sampleItem.setName("Test2");
        sampleItem.setNumber(2);
        types = new TreeSet<String>();
        types.add("Type2");
        sampleItem.setTypes(types);
        mapper = new DynamoDBMapper(ddb);
        mapper.save(sampleItem);
        
        mapper.save(sampleItem);
        sampleItem = new SampleItem();
        sampleItem.setName("Test3");
        sampleItem.setNumber(3);
        types = new TreeSet<String>();
        types.add("Type3");
        sampleItem.setTypes(types);
        mapper = new DynamoDBMapper(ddb);
        mapper.save(sampleItem);
        
        //
        
        ddb.shutdown();       

    }
    
    @DynamoDBTable(tableName="Sample")
    public static class SampleItem {
	    private String Name;
	    private int Number;
	    private Set<String> Types;
	
	    @DynamoDBHashKey(attributeName="Name")
	    public String getName() {
	        return Name;
	    }
	    public void setName(String Name) {
	        this.Name = Name;
	    }
	
	    @DynamoDBAttribute(attributeName="Number")
	    public int getNumber() {
	        return Number;
	    }
	    public void setNumber(int Number) {
	        this.Number = Number;
	    }
	
	    @DynamoDBAttribute(attributeName="Types")
	    public Set<String> getTypes() {
	        return Types;
	    }
	    public void setTypes(Set<String> Types) {
	        this.Types = Types;
	    }

	    @Override
	    public String toString() {
	        return "sampleItem [Name=" + Name 
	        	+ ", Number=" + Number
	            + ", Types=" + Types + "]";
	    }

    }
    
}