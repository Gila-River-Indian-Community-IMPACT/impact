package us.oh.state.epa.stars2.webcommon.converter;

import static org.junit.Assert.*;

import javax.faces.convert.ConverterException;

import org.junit.Test;



public class SigDigNumberConverterTest {
	
    @Test
    public void testValidCommaPattern() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "#,###.##";
        converter.setPattern(pattern);
        converter.setNonNumericAllowed(true);

        String value = "1234.56";
        assertEquals(value, converter.formatNumber(value));
        
        value = "1,234.56";
        assertEquals(value, converter.formatNumber(value));
        
        value = "1,234.50";
        assertEquals(value, converter.formatNumber(value));
        
        value = "0.00";
        assertEquals(value, converter.formatNumber(value));
        
        value = "1.23E2";
        assertEquals(value, converter.formatNumber(value));
        
        value = "12.34E-3";
        assertEquals(value, converter.formatNumber(value));
    }
    
    @Test
    public void testValidCommaPatternNumericOnly() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "#,###.##";
        converter.setPattern(pattern);

        String value = "1234.56";
        assertEquals(value, converter.formatNumber(value));
        
        value = "1,234.56";
        assertEquals(value, converter.formatNumber(value));
        
        value = "1,234.50";
        assertEquals(value, converter.formatNumber(value));
        
        value = "0.00";
        assertEquals(value, converter.formatNumber(value));
        
        value = "1.23E2";
        assertEquals(value, converter.formatNumber(value));
        
        value = "12.34E-3";
        assertEquals(value, converter.formatNumber(value));
    }
    
    @Test
    public void testInvalidCommaPattern() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "#,###.##";
        converter.setPattern(pattern);
        converter.setNonNumericAllowed(true);

        boolean exception = false;
        try {
            // too many fractional digits
            converter.formatNumber("123.3445");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);
        
        exception = false;
        try {
            // number too large
            converter.formatNumber("1233445");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);
        
        exception = false;
        try {
            // number too large
            converter.formatNumber("1.23E10");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);
        
        exception = false;
        try {
            // invalid number
            converter.formatNumber("abc");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);    
    }
    
    @Test
    public void testInvalidCommaPatternNumericOnly() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "#,###.##";
        converter.setPattern(pattern);

        boolean exception = false;
        try {
            // too many fractional digits
            converter.formatNumber("123.3445");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);
        
        exception = false;
        try {
            // number too large
            converter.formatNumber("1233445");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);
        
        exception = false;
        try {
            // number too large
            converter.formatNumber("1.23E10");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);
        
        exception = false;
        try {
            // invalid number
            converter.formatNumber("abc");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);    
    }
    
    @Test
    public void testValidExponentPattern() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "#,###.##E0";
        converter.setPattern(pattern);
        converter.setNonNumericAllowed(true);

        String value = "1234.56";
        assertEquals(value, converter.formatNumber(value));
        
        value = "1234.56";
        assertEquals(value, converter.formatNumber(value));
        
        value ="1234.50";
        assertEquals(value, converter.formatNumber(value));
        
        value = "0.00";
        assertEquals(value, converter.formatNumber(value));
        
        value = "123";
        assertEquals(value, converter.formatNumber(value));
        
        value = "123.4E-4";
        assertEquals(value, converter.formatNumber(value));
    }
    
    @Test
    public void testValidExponentPatternNumericOnly() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "#,###.##E0";
        converter.setPattern(pattern);

        String value = "1234.56";
        assertEquals(value, converter.formatNumber(value));
        
        value = "1234.56";
        assertEquals(value, converter.formatNumber(value));
        
        value ="1234.50";
        assertEquals(value, converter.formatNumber(value));
        
        value = "0.00";
        assertEquals(value, converter.formatNumber(value));
        
        value = "123";
        assertEquals(value, converter.formatNumber(value));
        
        value = "123.4E-4";
        assertEquals(value, converter.formatNumber(value));
    }
    
    @Test
    public void testInvalidExponentPattern() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "#,###.##E0";
        converter.setPattern(pattern);
        converter.setNonNumericAllowed(true);

        boolean exception = false;
        try {
            // too many fractional digits
            converter.formatNumber("123.3445");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);
        
        exception = false;
        try {
            // number too large
            converter.formatNumber("1233445");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);
        
        exception = false;
        try {
            // number too large
            converter.formatNumber("1.23E10");
        } catch (ConverterException e) {
            exception = true;
        }
        
        exception = false;
        try {
            // invalid number
            converter.formatNumber("abc");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);    
    }
    
    @Test
    public void testInvalidExponentPatternNumericOnly() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "#,###.##E0";
        converter.setPattern(pattern);

        boolean exception = false;
        try {
            // too many fractional digits
            converter.formatNumber("123.3445");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);
        
        exception = false;
        try {
            // number too large
            converter.formatNumber("1233445");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);
        
        exception = false;
        try {
            // number too large
            converter.formatNumber("1.23E10");
        } catch (ConverterException e) {
            exception = true;
        }
        
        exception = false;
        try {
            // invalid number
            converter.formatNumber("abc");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);    
    }
    
    @Test
    public void testValidNoCommaPattern() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "###.##";
        converter.setPattern(pattern);
        converter.setNonNumericAllowed(true);

        String value = "234.56";
        assertEquals(value, converter.formatNumber(value));
        
        value ="234.50";
        assertEquals(value, converter.formatNumber(value));
        
        value = "0.00";
        assertEquals(value, converter.formatNumber(value));
        
        value = "123";
        assertEquals(value, converter.formatNumber(value));
        
        value = "123.4E-4";
        assertEquals(value, converter.formatNumber(value));
    }
    
    @Test
    public void testValidNoCommaPatternNumericOnly() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "###.##";
        converter.setPattern(pattern);

        String value = "234.56";
        assertEquals(value, converter.formatNumber(value));
        
        value ="234.50";
        assertEquals(value, converter.formatNumber(value));
        
        value = "0.00";
        assertEquals(value, converter.formatNumber(value));
        
        value = "123";
        assertEquals(value, converter.formatNumber(value));
        
        value = "123.4E-4";
        assertEquals(value, converter.formatNumber(value));
    }

    @Test
    public void testInvalidNoCommaPattern() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "###.##";
        converter.setPattern(pattern);
        converter.setNonNumericAllowed(true);

        boolean exception = false;
        try {
            // too many fractional digits
            converter.formatNumber("123.3445");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);
        
        exception = false;
        try {
            // number too large
            converter.formatNumber("1233445");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);
        
        exception = false;
        try {
            // number too large (with comma)
            converter.formatNumber("1,234.56");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);      
        
        exception = false;
        try {
            // invalid number
            converter.formatNumber("abc");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);    
    }
    
    @Test
    public void testInvalidNoCommaPatternNumericOnly() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "###.##";
        converter.setPattern(pattern);

        boolean exception = false;
        try {
            // too many fractional digits
            converter.formatNumber("123.3445");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);
        
        exception = false;
        try {
            // number too large
            converter.formatNumber("1233445");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);
        
        exception = false;
        try {
            // number too large (with comma)
            converter.formatNumber("1,234.56");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);      
        
        exception = false;
        try {
            // invalid number
            converter.formatNumber("abc");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);    
    }
        
    @Test
    public void testValidWithNonNumeric() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "#,###.##";
        converter.setPattern(pattern);
        converter.setNonNumericAllowed(true);

        String value = "1234.56 mg";
        assertEquals(value, converter.formatNumber(value));
        
        value = "1,234.56 g/L";
        assertEquals(value, converter.formatNumber(value));
        
        value = "1,234.50 ms2";
        assertEquals(value, converter.formatNumber(value));
        
        value = "0.00 N";
        assertEquals(value, converter.formatNumber(value));
        
        value = "1.23E2 mm Hg";
        assertEquals(value, converter.formatNumber(value));
        
        value = "12.34E-3 barr";
        assertEquals(value, converter.formatNumber(value));
    }
        
    @Test
    public void testInalidWithNonNumeric() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "#,###.##";
        converter.setPattern(pattern);


        boolean exception = false;
        try {
            converter.formatNumber("1234.56 mg");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);   
        
        exception = false;
        try {
            converter.formatNumber("1,234.56 g/L");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);   
        
        exception = false;
        try {
            converter.formatNumber("1,234.50 ms2");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);   
        
        exception = false;
        try {
            converter.formatNumber("1,234.50 ms2");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);   
        
        exception = false;
        try {
            converter.formatNumber("0.00 N");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);   
        
        exception = false;
        try {
            converter.formatNumber("1.23E2 mm Hg");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);   
        
        exception = false;
        try {
            converter.formatNumber("12.34E-3 barr");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception);   
    }
    
    @Test
    public void testRanges() {
        SigDigNumberConverter converter = new SigDigNumberConverter();
        String pattern = "###.##";
        converter.setPattern(pattern);
        converter.setMaximumValue(999.99);
        converter.setMinimumValue(-100.0);
        
        String value = "100.0";
        converter.formatNumber(value);
        assertEquals(value, converter.formatNumber(value));
        
        value = "0";
        converter.formatNumber(value);
        assertEquals(value, converter.formatNumber(value));
        
        value = "999.99";
        converter.formatNumber(value);
        assertEquals(value, converter.formatNumber(value));

        value = "-100";
        converter.formatNumber(value);
        assertEquals(value, converter.formatNumber(value));
        
        // test error cases
        boolean exception = false;
        try {
            converter.formatNumber("1000");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception); 
        try {
            converter.formatNumber("-100.01");
        } catch (ConverterException e) {
            exception = true;
        }
        assertTrue(exception); 
    }
}
