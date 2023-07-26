package github.polarisink.scaffold.domain.step;


import github.polarisink.scaffold.domain.BaseJpaRepo;

import java.util.List;

/**
 *
 */
public interface StepRepo extends BaseJpaRepo<Step> {

    List<Step> findAllByArchivesIdIsNullAndTemplateId(Long templateId);
}
