/**
 * Copyright 2021 Unicon (R)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.unicon.lti.controller.lti;


import net.unicon.lti.service.lti.LTIDataService;
import net.unicon.lti.utils.TextConstants;
import net.unicon.lti.utils.oauth.OAuthUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Serving the public key of the tool.
 */
@Controller
@Scope("session")
@RequestMapping("/jwks")
public class JWKController {

    @Autowired
    LTIDataService ltiDataService;

    static final Logger log = LoggerFactory.getLogger(JWKController.class);

    @RequestMapping(value = "/jwk", method = RequestMethod.GET, produces = "application/json;")
    @ResponseBody
    public Map<String, List<Map<String, Object>>> jkw(HttpServletRequest req, Model model) throws GeneralSecurityException {
        Map<String, List<Map<String, Object>>> keys = new HashMap<>();
        log.debug("Someone is calling the jwk endpoint!");
        log.debug(req.getQueryString());
        RSAPublicKey toolPublicKey = OAuthUtils.loadPublicKey(ltiDataService.getOwnPublicKey());
        Map<String, Object> values = new HashMap<>();
        values.put("kty", toolPublicKey.getAlgorithm()); // getAlgorithm() returns kty not algorithm
        values.put("kid", TextConstants.DEFAULT_KID);
        values.put("n", Base64.getUrlEncoder().encodeToString(toolPublicKey.getModulus().toByteArray()));
        values.put("e", Base64.getUrlEncoder().encodeToString(toolPublicKey.getPublicExponent().toByteArray()));
        values.put("alg", "RS256");
        values.put("use", "sig");
        List<Map<String, Object>> valuesList = new ArrayList<>();
        valuesList.add(values);
        keys.put("keys", valuesList);
        return keys;
    }
}
