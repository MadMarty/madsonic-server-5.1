package org.madsonic.controller;

import org.madsonic.service.SettingsService;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for the ultimate pandora box.
 *
 * @author Madevil
 */
public class PandoraSettingsController extends ParameterizableViewController {

    private SettingsService settingsService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {

        Map<String, Object> map = new HashMap<String, Object>();

        if (isFormSubmission(request)) {
            String error = handleParameters(request);
            map.put("error", error);
            if (error == null) {
                map.put("reload", true);
            }
        }
        ModelAndView result = super.handleRequestInternal(request, response);
        
        map.put("similarAlbumtitle", settingsService.getPandoraResultAlbum());
        map.put("similarArtists", settingsService.getPandoraResultArtist());
        map.put("similarArtistsTopTrack", settingsService.getPandoraResultArtistTopTrack());
        map.put("similarGenre", settingsService.getPandoraResultGenre());
        map.put("similarMood", settingsService.getPandoraResultMood());
        map.put("similarOther", settingsService.getPandoraResultSimilar());

        result.addObject("model", map);
        return result;
    }

    /**
     * Determine if the given request represents a form submission.
     *
     * @param request current HTTP request
     * @return if the request represents a form submission
     */
    private boolean isFormSubmission(HttpServletRequest request) {
        return "POST".equals(request.getMethod());
    }

    private String handleParameters(HttpServletRequest request) {

        String similarAblumtitle = StringUtils.trimToNull(request.getParameter("similarAlbumtitle"));
        String similarArtists = StringUtils.trimToNull(request.getParameter("similarArtists"));
        String similarArtistsTopTrack = StringUtils.trimToNull(request.getParameter("similarArtistsTopTrack"));
        String similarGenre = StringUtils.trimToNull(request.getParameter("similarGenre"));
        String similarMood = StringUtils.trimToNull(request.getParameter("similarMood"));
        String similarOther = StringUtils.trimToNull(request.getParameter("similarOther"));
        
        settingsService.setPandoraResultAlbum(Integer.valueOf(similarAblumtitle));
        settingsService.setPandoraResultArtist(Integer.valueOf(similarArtists));
        settingsService.setPandoraResultArtistTopTrack(Integer.valueOf(similarArtistsTopTrack));
        settingsService.setPandoraResultGenre(Integer.valueOf(similarGenre));
        settingsService.setPandoraResultMood(Integer.valueOf(similarMood));
        settingsService.setPandoraResultSimilar(Integer.valueOf(similarOther));
        
        settingsService.save();
        
        return null;
    }

    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

}
