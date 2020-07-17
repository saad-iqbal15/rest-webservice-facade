package com.webservice.example;



import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Slf4j
@RefreshScope
@RequestMapping ("/userApi")
@Api(value = "User facade controller", tags= "User data facade API")
public class UserServiceFacadeController {

	private static Logger logger = LoggerFactory.getLogger(UserServiceFacadeController.class);
	@Autowired
	UserRepositoryH2 repositoryH2;
	
	@Autowired
	ExcepHandler excepHandler;
	
	@Value("${getAllUsers.errorMessage}")
	String getAllUsersError;
	
	@Value("${getUserbyId.errorMessage}")
	String getUserbyIdError;

	@Value("${addUser.errorMessage}")
	String addUserError;

	@Value("${addUser.successMessage}")
	String addUserSuccess;
	
	@Value("${updateUser.errorMessage}")
	String updateUserError;
	
	@Value("${updateUser.successMessage}")
	String updateUserSuccess;
	
	@Value("${deleteUserbyId.errorMessage}")
	String deleteUserbyIdError;
	
	@Value("${deleteUserbyId.successMessage}")
	String deleteUserbyIdSuccess;
	
	@GetMapping(value = "/getAllUsers", produces= "application/json")
	@ApiOperation(value = "Get details of all users from facade api")
	@ApiResponses(value= {
	@ApiResponse(code = 404, message = "Records could not be found"),
	@ApiResponse(code = 500, message = "Cannot establish connection with facade api"),
	@ApiResponse(code = 200, message = "OK")})
	                                       
	public ResponseEntity<String> getAllUsers() throws JSONException {
		try {
			
		List<User> allUsers= repositoryH2.findAll();
			logger.info("All Users has been fetched from database");
		if (allUsers.isEmpty()) {
			throw new CustomException (getAllUsersError, HttpStatus.NOT_FOUND);
		} 
		else {
			
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String jsonString = gson.toJson(allUsers);


		return new ResponseEntity<>(jsonString, HttpStatus.OK);
		}
		}
		catch (CustomException exception) {
			return excepHandler.exceptionMessage(exception);
		}
	}

	
	@GetMapping(value = "/getUser", produces= "application/json")
	@ApiOperation(value = "Get details of single user from facade api")
	@ApiResponses(value= {
	@ApiResponse(code = 404, message = "User is not present"),
	@ApiResponse(code = 200, message = "OK")})
	@ApiImplicitParam(name = "id", dataType = "String",
    required = true, 
	  defaultValue = "1"
	  )
	public ResponseEntity<String> getUserbyId(@RequestParam (value="id") Long id) throws JSONException {
		try {
		User user = repositoryH2.findById(id).get();
			logger.info("User with specific id has been fetched from database");
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String jsonString = gson.toJson(user);
		return new ResponseEntity<>(jsonString, HttpStatus.OK); 
		}
		catch(Exception exception) {
			try {
			throw new CustomException (getUserbyIdError, HttpStatus.NOT_FOUND);
			}
			catch (CustomException ex) {
				return excepHandler.exceptionMessage(ex);
			}
		}
		
	}
	
    @PostMapping(value = "/addUser", produces = "application/json")
    @ApiOperation(value = "Addu new user into databse from facade api")
	@ApiResponses(value= {
	@ApiResponse(code = 400, message = "Bad request to add user"),
	@ApiResponse(code = 202, message = "User request accepted and created")})
    public ResponseEntity<String> addUser (@RequestBody User newUser) throws JSONException {   
	try {
    	if (!(StringUtils.isEmpty(newUser.getFirstName()) && StringUtils.isEmpty(newUser.getLastName()))) {
    	 repositoryH2.save(newUser);
			logger.info("User has been added into database");
    	 JSONObject obj = new JSONObject();
    	 obj.put("Message", addUserSuccess);
    	 return new ResponseEntity<>(obj.toString(), HttpStatus.ACCEPTED);
	}
	else {
		throw new CustomException (addUserError, HttpStatus.BAD_REQUEST);
	}
	}
	catch(Exception exception) {
		try {
			
		throw new CustomException (exception.getMessage(), HttpStatus.BAD_REQUEST);
		}
		catch (CustomException ex) {
			return excepHandler.exceptionMessage(ex);
		}
	}
		
	}
   
    
	

    @PutMapping(value = "/updateUser/{id}", produces = "application/json")
    @ApiOperation(value = "Modify existing user into databse from facade api")
   	@ApiResponses(value= {
   	@ApiResponse(code = 400, message = "Bad request to modify user"),
   	@ApiResponse(code = 200, message = "User modified succesfully")})
    @ApiImplicitParam(name = "id", dataType = "String",
    required = true, 
	  defaultValue = "1"
	  )
	public ResponseEntity<String> updateUser(@RequestBody User newUser, @PathVariable Long id) throws JSONException{
	 try {
		if (StringUtils.isEmpty(newUser.getFirstName()) && StringUtils.isEmpty(newUser.getLastName()) 
				&& StringUtils.isEmpty(newUser.getPhone())) {
			throw new CustomException (updateUserError, HttpStatus.BAD_REQUEST);
		}
		else {
		 repositoryH2.findById(id).map(user -> {
	    if (!StringUtils.isEmpty(newUser.getFirstName())) {
		user.setFirstName(newUser.getFirstName());
	    }
	    if (!StringUtils.isEmpty(newUser.getLastName())) {
		user.setLastName(newUser.getLastName());
	    }
	    if (!StringUtils.isEmpty(newUser.getPhone())) {
		user.setPhone(newUser.getPhone());
	    }
        return repositoryH2.save(user);
    }).orElseGet(() -> {
    	newUser.setId(id);
        return repositoryH2.save(newUser);
    });
			logger.info("User has been updated into database");
		 JSONObject obj = new JSONObject();
    	 obj.put("Message", updateUserSuccess);
    	 return new ResponseEntity<>(obj.toString(), HttpStatus.OK);
		 
	 }
	 }
	 catch(Exception exception) {
			try {
				throw new CustomException (exception.getMessage(), HttpStatus.BAD_REQUEST);
			}
			catch (CustomException ex) {
				return excepHandler.exceptionMessage(ex);
			}
		}
}
    
    
    @DeleteMapping(value = "/removeUser/{id}", produces = "application/json")
    @ApiOperation(value = "Remove user from data source")
	@ApiResponses(value= {
	@ApiResponse(code = 404, message = "User is not present"),
	@ApiResponse(code = 200, message = "OK")})
    @ApiImplicitParam(name = "id", dataType = "String",
    required = true, 
	  defaultValue = "1"
	  )
    public ResponseEntity<String> deleteUserbyId(@PathVariable Long id) throws JSONException {
    	try {
    	 repositoryH2.deleteById(id);
			logger.info("User has been removed into database");
    	 JSONObject jobj = new JSONObject();
    	 jobj.put("Message", updateUserSuccess);
    	return new ResponseEntity<>(jobj.toString(), HttpStatus.OK);
    	}
    	catch(Exception exception) {
			try {
			throw new CustomException (deleteUserbyIdError, HttpStatus.NOT_FOUND);
			}
			catch (CustomException ex) {
				return excepHandler.exceptionMessage(ex);
			}
		}
    }
}

