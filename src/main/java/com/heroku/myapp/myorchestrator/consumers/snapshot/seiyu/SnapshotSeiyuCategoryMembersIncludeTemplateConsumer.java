package com.heroku.myapp.myorchestrator.consumers.snapshot.seiyu;

import static com.heroku.myapp.commons.config.enumerate.Kind.seiyu_category_members;
import static com.heroku.myapp.commons.config.enumerate.Kind.seiyu_template_include_pages;
import com.heroku.myapp.commons.consumers.SnapshotQueueConsumer;
import com.heroku.myapp.commons.util.actions.MasterUtil;
import com.heroku.myapp.commons.util.content.DocumentUtil;
import com.heroku.myapp.commons.util.content.MapList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.camel.Exchange;
import org.bson.Document;
import org.springframework.stereotype.Component;

@Component
public class SnapshotSeiyuCategoryMembersIncludeTemplateConsumer extends SnapshotQueueConsumer {

    @Override
    protected Optional<Document> doSnapshot(Exchange exchange) {
        try {
            MasterUtil util = new MasterUtil(exchange);
            Document scm, stp;
            scm = util.findOrElseThrow(seiyu_category_members);
            stp = util.findOrElseThrow(seiyu_template_include_pages);
            List<Map<String, Object>> productByTitle
                    = new MapList(scm).productByTitle(new MapList(stp));
            return new DocumentUtil(productByTitle).nullable();
        } catch (Exception ex) {
            util().sendError("doSnapshot", ex);
            return Optional.empty();
        }
    }
}
