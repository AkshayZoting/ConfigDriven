var gridOptions;
function initGridData(columnDefs,rowData){
	console.log(rowData)
	gridOptions = {
		defaultColDef: {
	        sortable: true,
	        editable:true
	    },
	  columnDefs: columnDefs,
	  rowData: rowData,
	  pagination: true
	};
	
	$('#myGrid').empty();
	var eGridDiv = document.querySelector('#myGrid');
	new agGrid.Grid(eGridDiv, gridOptions);
}

function onSaveData(){
	var selectedData = [];
    if(gridOptions){
        gridOptions.api.stopEditing();
        gridOptions.api.forEachNode(function (rowNode, index) {
            if(rowNode && rowNode.data){
                selectedData.push(rowNode.data);
            }

        });
        saveGridData([ {
			name : 'gridData',
			value : JSON.stringify(selectedData)
		} ]);
       
    }
}



function displayHighCharts(xAxis,yAxis,xAxisVal,yAxisVal){
 
    Highcharts.chart('dataGridContaineer', {
    	
    	chart: {
            type: 'line'
        },

        title: {
            text: xAxis + " VS " + yAxis
        },

        yAxis: {
            title: {
                text: yAxis
            }
        },
        xAxis: {
            accessibility: {
                value: xAxisVal
            },
            title: {
                text: xAxis
            },
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'middle'
        },

        series: [{
            name: yAxis,
            data: yAxisVal
        }],
    });
}



