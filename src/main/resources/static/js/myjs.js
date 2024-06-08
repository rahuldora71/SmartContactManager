console.log("THis is script file of smart contact manager site");
// $("#menubtn").hide();
// $(".sidebar").show();
// const toggleSidebar = () =>{

//     if($(".sidebar").is(":visible"))
//         {
//             $(".sidebar").css("display","none");
//             $(".content").css("margin-left","0%");
            
//             $('.sidebar').hide();
//         }else{
//             $(".sidebar").css("display","block");
//             $(".content").css("margin-left","20%");
            
//             $('.sidebar').show();
//         }
// };
const toggleSidebar = () => {

    if ($(".sidebar").is(":visible")) {
        $(".sidebar").hide();
        $(".content").css("margin-left", "0%");
        $("#menubtn").show();

    } else {
        $(".sidebar").show();
        $(".content").css("margin-left", "20%");
        $("#menubtn").hide();
    }
};
