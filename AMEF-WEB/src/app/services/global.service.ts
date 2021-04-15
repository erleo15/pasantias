export function nameMonth(value){
    var year = value.substring(0,4);
    var month = value.substring(5,7);
    var monthStr; 
    switch(month){
        case "01" : monthStr = "Enero"; break;
        case "02" : monthStr = "Febrero"; break;
        case "03" : monthStr = "Marzo"; break;
        case "04" : monthStr = "Abril"; break;
        case "05" : monthStr = "Mayo"; break;
        case "06" : monthStr = "Junio"; break;
        case "07" : monthStr = "Julio"; break;
        case "08" : monthStr = "Agosto"; break;
        case "09" : monthStr = "Septiembre"; break;
        case "10" : monthStr = "Octubre"; break;
        case "11" : monthStr = "Noviembre"; break;
        case "12" : monthStr = "Deciembre"; break;
    }
    return monthStr +" " + year;
}
