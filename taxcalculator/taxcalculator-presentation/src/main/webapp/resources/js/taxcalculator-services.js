var taxcalculatorServices = angular.module('taxcalculatorServices', ['ngResource']);

taxcalculatorServices
  .factory('JobResultsResource', [ '$resource',
    function ($resource) {
      return $resource('/taxcalculator/rest/jobResults', {}, {'query': {method: 'GET', isArray: true}});
    }
  ])
  .factory('EmployeesOverviewResource', [ '$resource',
    function ($resource) {
      return $resource('/taxcalculator/rest/employees', {}, {'query': {method: 'GET', isArray: true}});
    }
  ])
  .factory('RunJobResource', [ '$resource',
    function ($resource) {
      return $resource('/taxcalculator/rest/runJob/:year/:month', {}, {run: {method: 'POST', params: {year: '@year', month: '@month'}}});
    }
  ])
