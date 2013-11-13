package openbus.kafka.avro;

/*
* Copyright 2013 Produban
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.StringTokenizer;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.log4j.Logger;



/**
 * A simple Avro Serializer with schema embedded
 * Takes String with delimited avro field list
 */
public class AvroSchemaSerializer implements kafka.serializer.Encoder  {

	static final Logger logger = Logger.getLogger(AvroSchemaSerializer.class);

    private Schema schema;
    private String[] fields;    
    
	/**
	 * 
	 * @param schemaIs InputStream with schema bytes
	 * @param fields list for encoding
	 */
    public AvroSchemaSerializer(InputStream schemaIs, String[] fields) {
    	setSchema(schemaIs);
    	this.fields=fields;
	}
    
   
    private void setSchema(InputStream schemaIs) {
        Schema.Parser parser = new Schema.Parser();
        try {
			//schema = parser.parse(getClass().getClassLoader().getResourceAsStream("apacheLog.avsc"));
        	schema = parser.parse(schemaIs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * 
     * @param sch
     * @param fields
     */
    public AvroSchemaSerializer(Schema sch, String[] fields) {
			this.schema = sch;
	    	this.fields=fields;
	}

    /**
     * Encodes a delimited string 
     * @param str
     * @param delimeter
     * @return
     */
	public byte[] toBytes(String str, String delimeter) {
		

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(
                schema);
        DataFileWriter<GenericRecord> dataFileWriter = new DataFileWriter<GenericRecord>(
                writer);
        try {
			dataFileWriter.create(schema, os);

	        GenericRecord datum = new GenericData.Record(schema);
	        String[] st = str.split(delimeter);
	        int i=0;
	        for(String s : st){
	        	datum.put(fields[i++], s);
	        }
	        
	        dataFileWriter.append(datum);
	        dataFileWriter.close();
	        
	        logger.info("encoded string: " + os.toString());
	        os.close();

        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
              
        logger.info("size: " + os.size());
        return os.toByteArray();	
        
    }

    /**
     * Encodes an space-delimited object tostring 
     */
	public byte[] toBytes(Object arg0) {
		return this.toBytes(arg0.toString(),"_#_");
	}

}
