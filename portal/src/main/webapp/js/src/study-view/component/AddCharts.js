/*
 * Copyright (c) 2015 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/


var AddCharts = (function(){
    var liClickCallback;
    
    function addEvents() {
        $('#study-view-add-chart ul').hide();
        $('#study-view-add-chart').hover(function(){
           $('#study-view-add-chart ul').stop().show(300);
        }, function(){
           $('#study-view-add-chart ul').stop().hide(300);
        });
    }
    
    function createDiv() {
        $("#study-view-header-function").append(StudyViewBoilerplate.addChartDiv);
    }
    
    function initAddChartsButton(_param) {
        var _name = _param.name,
            _nameKeys = Object.keys(_name),
            _nameKeyLength = _nameKeys.length,
            _dispalyedID = _param.displayedID,
            _displayName = _param.displayName,
            _showedNames = [],
            _showedNamesLength = 0;
        
//        $('#study-view-add-chart ul').find('li').remove().end();
        $('#study-view-add-chart')
                .find('option')
                .remove()
                .end()
                .append('<option id="">Add Chart</option>');
        
        for ( var i = 0 ; i < _nameKeyLength; i++) {
            if(_dispalyedID.indexOf(_name[_nameKeys[i]]) === -1){
                var _datum = {};
                _datum.displayName = _displayName[_nameKeys[i]];
                _datum.name = _name[_nameKeys[i]];
                _showedNames.push(_datum);
            }
        }
        
        _showedNamesLength = _showedNames.length;
        
        _showedNames.sort(function(a, b){
            var _aValue = a.displayName.toUpperCase();
            var _bValue = b.displayName.toUpperCase();
            
            return _aValue.localeCompare(_bValue);
        });
        
        for(var i = 0; i < _showedNamesLength; i++){
//            $('#study-view-add-chart ul')
//                    .append($("<li></li>")
//                        .attr("id",_showedNames[i].name)
//                        .text(_showedNames[i].displayName));
            $('#study-view-add-chart')
                .append($("<option></option>")
                    .attr("id",_showedNames[i].name)
                    .text(_showedNames[i].displayName));
        }
        
        
//        if($('#study-view-add-chart ul').find('li').length === 0 ){
        if($('#study-view-add-chart').find('option').length === 1){
            $('#study-view-add-chart').css('display','none');
        }else{
            bindliClickFunc();
        }
    }
    
    function bindliClickFunc() {
//        $('#study-view-add-chart ul li').unbind('click');
//        $('#study-view-add-chart ul li').click(function() {
        $('#study-view-add-chart').unbind('change');
        $('#study-view-add-chart').change(function() {
//            var _id = $(this).attr('id'),
//                _text = $(this).text();
            var _id = $(this).children(":selected").attr('id'),
                _text = $(this).children(":selected").text();
            liClickCallback(_id, _text);
        });
    }
    return {
        init: function() {
            createDiv();
//            addEvents();
        },
        
        initAddChartsButton: initAddChartsButton,
        
        bindliClickFunc: bindliClickFunc,
        
        liClickCallback: function(_callback) {
            liClickCallback = _callback;
        }
    };
})();

