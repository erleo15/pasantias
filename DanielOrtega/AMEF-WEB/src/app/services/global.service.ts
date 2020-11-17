export function nameMonth(value){
    var year = value.substring(0,4);
    var month = value.substring(5,7);
    var monthStr; 
    switch(month){
        case "01" : monthStr = "January"; break;
        case "02" : monthStr = "February"; break;
        case "03" : monthStr = "March"; break;
        case "04" : monthStr = "April"; break;
        case "05" : monthStr = "May"; break;
        case "06" : monthStr = "June"; break;
        case "07" : monthStr = "July"; break;
        case "08" : monthStr = "August"; break;
        case "09" : monthStr = "September"; break;
        case "10" : monthStr = "October"; break;
        case "11" : monthStr = "November"; break;
        case "12" : monthStr = "December"; break;
    }
    return monthStr +" " + year;
}
