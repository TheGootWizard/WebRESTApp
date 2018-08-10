var webRestApp = angular.module('webRestApp',['ngRoute']);

webRestApp.config(['$routeProvider',function($routeProvider){

  $routeProvider
  .when('/home', {
    templateUrl: 'views/home.html',
    controller: 'HomeController'
  })
  .when('/login',{
    templateUrl: 'views/login.html',
    controller:'LoginController'
  })
  .when('/register',{
    templateUrl: 'views/register.html',
    controller: 'RegisterController'
  })
  .when('/add_operator',{
    templateUrl: 'views/add_operator.html',
    controller: 'AddOperatorController'
  }).otherwise({
    redirectTo:'/home'
  });

}]);

webRestApp.run(function(){

});

webRestApp.controller('HeaderController',['$scope',function($scope){

}]);

webRestApp.controller('HomeController',['$scope','$http',function($scope,$http){
  $scope.presentUser=function(){
    $scope.currentuser={
      username:"",
      password:""
    };
    $http.get('http://localhost:8080/RESTApp/rest/user').then(function(response){
      $scope.currentuser=response.data;
    });
  };
  $scope.testingMethod=function(){
    console.log(currentuser);
  };
}]);

webRestApp.controller('LoginController',['$scope','$http',function($scope,$http){
  $scope.loginUser=function(){
    $scope.logintrialuser={
      username: $scope.probableuser.username,
      password: $scope.probableuser.password
    };
    $scope.probableuser.username="";
    $scope.probableuser.password="";

    var formatedUser=JSON.stringify($scope.logintrialuser);
    console.log(formatedUser);
    $http.post('http://localhost:8080/RESTApp/rest/login',formatedUser).then(function(formatedUser,status){
      console.log('Login manifested successfully!');
    });
   };
}]);

webRestApp.controller('RegisterController',['$scope','$http',function($scope,$http){
  $scope.registerUser=function(){
    $scope.newuser={
      username: $scope.userbeingregistered.username,
      password: $scope.userbeingregistered.password,
      email: $scope.userbeingregistered.email,
      country: $scope.userbeingregistered.country
    };
    $scope.userbeingregistered.username="";
    $scope.userbeingregistered.password="";
    $scope.userbeingregistered.email="";
    $scope.userbeingregistered.country="";

    var formatedUser=angular.toJson($scope.newuser);
    $http.post('http://localhost:8080/RESTApp/rest/register',formatedUser).then(function(formatedUser,status){
      console.log('New user registered successfully!');
    });

  };
}]);

webRestApp.controller('AddOperatorController',['$scope',function($scope){
  $scope.username = "";
  $scope.password = "";
  $scope.addOperator=function(){

  };
}]);
