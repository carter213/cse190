TO DO:
  phone# standard convention ?

cse190
======
createFood: 
  Parameters:
    name - required
    rest_id - required
    description - NOT required
  On success: prints "Success"
  Errors: "Error, parameter missing" if any are missing
          "Error, duplicate found" if there already exists

findFood:
  Parameters:
    alg - NOT required; algorithm on what food you want returned. defaults to best 3 if present
    rest_id - required
  On success:
    prints JSON: [foodObj, foodObj, foodObj]
      foodObj = { food_id:value, name:value, vote:value, description:value }

  Errors: "Error, parameter missing"

createRestaurant:
  Parameters:
    name - required
    address - required
    city - required
    state - required
    zip - required
    phone - required
    website - NOT required
  On success: print "Success"
  Errors: "Error, duplicate found"

findRestaurants:
  Parameters:
    key - required, "search term"
    latitude - NOT required
    longitude - NOT required
    zip - NOT required
    city - NOT required
  On success:
    prints JSON: [restaurantObj, ..]
      restaurantObj = { id:value, name:value, address:value, city:value, state:value, latitude:value, longitude:value, phone:value, website:value, distance:value }
  Errors: 

createUser:
  Parameters:
    username - required
    email - required
    password - required
    first_name - required
    last_name - required
  On success: prints "TRUE"
  Errors: prints "NULL"

login:
  Parameters:
    username - required OR NOT required if email present
    email - required OR NOT required if username present
    password - required
  On success: prints user_id as string
  Errors: "False" if username or email not present
          "NULL" if username does not exist or password fails

updateUser:
  Parameters:
  NOT WORKING

createVote:
  Parameters:
    food_id - required
    user_id - required
    rest_id - required
    comment - NOT required
  On success: prints "Success"
  Errors: "Error, parameter missing"
