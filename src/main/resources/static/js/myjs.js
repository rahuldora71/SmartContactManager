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

const search = () => {
    //  console.log("search....");
    let query = $("#search-input").val();
    if (query == "") {
        $(".search-result").hide();

    } else {
        //Search
        //sending request to server
        let url = `http://localhost:9998/search/${query}`;
        fetch(url).then((reaponse) => {

            return reaponse.json();
        }).then((date) => {
            //data

            let text=`<div class='list-group'>`;

            date.forEach((contact)=>{
               text+=`<a   href='/user/${contact.cId}/contact' class='list-group-item list-group-item-action '>
                    <img class="profile_picture"  src="/images/${contact.image}" alt=" ">

${contact.name+' '+contact.secondName}</a>`
            });
            text+=`</div>`
            $(".search-result").html(text);

        $('.search-result').show();

        });
    }
};

// const focusOut = () => {
//
//     // $("#search-input").val("");
//
//         if (!$("#search-result").is(":focus") && !$("#search-input").is(":focus")) {
//
//             $("#search-input").val("");
//             $(".search-result").hide();
//
//
//         }
//         else if ($("#search-result").is(":focus") && !$("#search-input").is(":focus")) {
//             $("#search-input").val("");
//
//         }
//
// };

