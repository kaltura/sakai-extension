package org.sakaiproject.kaltura.services;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.site.api.SiteService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;

/**
 * Created by enietzel on 6/17/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class SecurityServiceTest {
    @Mock
    private AuthCodeService authCodeService;
    @Mock
    private DeveloperHelperService developerHelperService;
    @Mock
    private ServerConfigurationService serverConfigurationService;
    @Mock
    private SiteService siteService;

    private SecurityService securityService;
    public static final String AUTH_CODE = "cd268d82-2345-4239-8e33-6fd771086b64";

    @Before
    public void setup() {
        securityService = new SecurityService();
        securityService.setAuthCodeService(authCodeService);
        securityService.setDeveloperHelperService(developerHelperService);
        securityService.setServerConfigurationService(serverConfigurationService);
        securityService.setSiteService(siteService);
    }

    @Test
    public void securityCheck() throws Exception {
        when(authCodeService.isValid(anyString(), anyString())).thenReturn(true);
        when(developerHelperService.isUserAdmin(anyString())).thenReturn(false);

        try {
            securityService.securityCheck(AUTH_CODE, "jSmith");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void securityCheckFails() throws Exception {
        when(authCodeService.isValid(anyString(), anyString())).thenReturn(false);
        when(developerHelperService.isUserAdmin(anyString())).thenReturn(false);
        when(serverConfigurationService.getString("kaltura.authorization.override.code")).thenReturn("");

        try {
            securityService.securityCheck(AUTH_CODE, "jSmith");
        } catch (Exception e) {
            assertEquals(SecurityException.class, e.getClass());
            return;
        }
        fail("Should never get here");
    }

    @Test
    public void securityCheckWithOverride() throws Exception {
        when(authCodeService.isValid(anyString(), anyString())).thenReturn(false);
        when(developerHelperService.isUserAdmin(anyString())).thenReturn(false);
        when(serverConfigurationService.getString("kaltura.authorization.override.code")).thenReturn(AUTH_CODE);

        try {
            securityService.securityCheck(AUTH_CODE, "jSmith");
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void securityCheckWithOverrideFails() throws Exception {
        when(authCodeService.isValid(anyString(), anyString())).thenReturn(false);
        when(developerHelperService.isUserAdmin(anyString())).thenReturn(false);
        when(serverConfigurationService.getString("kaltura.authorization.override.code")).thenReturn("");

        try {
            securityService.securityCheck(AUTH_CODE, "jSmith");
        } catch (Exception e) {
            assertEquals(SecurityException.class, e.getClass());
            return;
        }
        fail("Should never get here");
    }
}