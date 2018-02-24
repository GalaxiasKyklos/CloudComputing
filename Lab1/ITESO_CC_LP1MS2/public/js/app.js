$(document).ready(function() {
  $("#version").html("v0.14");
  let results = [];
  let currentPage = 0;
  let total = 0;
  
  $("#searchbutton").click( function (e) {
    displayModal();
  });
  
  $("#searchfield").keydown( function (e) {
    if(e.keyCode == 13) {
      displayModal();
    }	
  });
  
  function displayModal() {
    $("#myModal").modal('show');

    $("#status").html("Searching...");
    $("#dialogtitle").html("Search for: "+$("#searchfield").val());
    $("#previous").hide();
    $("#next").hide();
    $.getJSON('/search/' + $("#searchfield").val() , function(data) {
      results = [];
      renderQueryResults(data);
    });
  }
  
  $("#next").click( function(e) {
    $('#previous').show();
    updatePage(++currentPage);
    if (currentPage * 4 >= total - 4) {
      $("#next").hide();
    }
  });
  
  $("#previous").click( function(e) {
    updatePage(--currentPage);
    if (currentPage * 4 < total) {
      $("#next").show();
    }
    if (currentPage === 0) {
      $('#previous').hide();
    }
  });

  function renderQueryResults(data) {
    
    if (data.error != undefined) {
      $("#status").html("Error: "+data.error);
    } else {
      $("#status").html(""+data.num_results+" result(s)");
      data.results.map(r => {
          results.push(`<img src="${r}" width="100" heigh="auto">`);
      });
      updatePage(currentPage);
      total = results.length;
      if (total > 4) {
        $("#next").show();
      }
     }
   }

   function updatePage(page) {
    $("td[id^='photo']").each((el, i) => {
      $(i).empty();
    });
    results.slice(page * 4, page * 4 + 4).map((r, i) => {
      $('#photo' + i).empty();
      $('#photo' + i).append(r);
    });
   }
});
