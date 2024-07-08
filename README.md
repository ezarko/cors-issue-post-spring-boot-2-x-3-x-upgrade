Since upgrading from Spring Boot 2.x to 3.x I am having these Spring Security related issues. I have boiled it down to the essentials in this github repo.

In the SpringBoot2 version all the tests pass. In the SpringBoot3 version I am seeing failures. I am having trouble discerning if this is an issue with my code/test, or if I have stumbled across a bug. I suspect the former.

In the testCors test, the third assertion, which is for an CORS violation which should return FORBIDDEN, is failing as OK is returned. In the testErrorHandlingUnauthorized test, the assertion is failing because it is expecting back an UNAUTHORIZED and it is getting a FORBIDDEN.
