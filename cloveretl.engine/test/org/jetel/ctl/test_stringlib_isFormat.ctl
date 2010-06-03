string test;
boolean isBlank;
string blank; 
boolean isBlank1;
string nullValue;
boolean isBlank2;
boolean isAscii1;
boolean isAscii2;
boolean isNumber;
boolean isNumber1;
boolean isNumber2;
boolean isNumber3;
boolean isNumber4;
boolean isNumber5;
boolean isNumber6;
boolean isInteger;
boolean isInteger1;
boolean isInteger2;
boolean isInteger3;
boolean isLong;
boolean isDate5;
boolean isDate6;
boolean isDate3;
boolean isDate;
boolean isDate1;
boolean isDate2;
boolean isDate4;
boolean isDate7;
boolean isDate8;
boolean isDate9;
boolean isDate10;
boolean isDate11; 
boolean isDate12;
boolean isDate13;
boolean isDate14;
boolean isDate15; 
boolean isDate16;
boolean isDate17;
boolean isDate18;

function integer transform() {
	test='test';
	isBlank=isBlank(test);
	blank = ''; 
	isBlank1=isBlank(blank);
	nullValue=null;
	isBlank2=isBlank(nullValue);
	isAscii1=isAscii('test');
	isAscii2=isAscii('aęř');
	isNumber=isNumber('t1');
	isNumber1=isNumber('1g');
	isNumber2=isNumber('1');
	printErr(str2integer('1'));
	isNumber3=isNumber('-382.334');
	printErr(str2double('-382.334'));
	isNumber4=isNumber('+332e2');
	isNumber5=isNumber('8982.8992e-2');
	printErr(str2double('8982.8992e-2'));
	isNumber6=isNumber('-7888873.2E3');
	printErr(str2decimal('-7888873.2E3'));
	isInteger=isInteger('h3');
	isInteger1=isInteger('78gd');
	isInteger2=isInteger('8982.8992');
	isInteger3=isInteger('-766542378');
	printErr(str2integer('-766542378'));
	isLong=isLong('7864232568822234');
	isDate5=isDate('20Jul2000','ddMMMyyyy','en.US');
	printErr(str2date('20Jul2000','ddMMMyyyy','en.GB'));
	isDate6=isDate('20July    2000','ddMMMMMMMMyyyy','en.US');
	printErr(str2date('20July    2000','ddMMMyyyy','en.GB'));
	isDate3=isDate('4:42','HH:mm');
	printErr(str2date('4:42','HH:mm'));
	isDate=isDate('20.11.2007','dd.MM.yyyy');
	printErr(str2date('20.11.2007','dd.MM.yyyy'));
	isDate1=isDate('20.11.2007','dd-MM-yyyy');
	isDate2=isDate('24:00 20.11.2007','kk:mm dd.MM.yyyy');
	isDate4=isDate('test 20.11.2007','hhmm dd.MM.yyyy');
	isDate7=isDate('                ','HH:mm dd.MM.yyyy',true);
	isDate8=isDate('                ','HH:mm dd.MM.yyyy');
	isDate9=isDate('20-15-2007','dd-MM-yyyy');
	isDate10=isDate('20-15-2007','dd-MM-yyyy',false);
	isDate11=isDate('20-15-2007','dd-MM-yyyy',true); 
	isDate12=isDate('942-12-1996','dd-MM-yyyy','en.US',true);
	isDate13=isDate('942-12-1996','dd-MM-yyyy','en.US',false);
	isDate14=isDate('12-Prosinec-1996','dd-MMM-yyyy','cs.CZ',false);
	isDate15=isDate('12-Prosinec-1996','dd-MMM-yyyy','en.US',false); 
	isDate16=isDate('24:00 20.11.2007','HH:mm dd.MM.yyyy');
	isDate17=isDate('','HH:mm dd.MM.yyyy',false);
	isDate18=isDate('','HH:mm dd.MM.yyyy',true);
	return 0;
}