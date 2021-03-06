package com.myapp.storing;

import org.omnifaces.cdi.ViewScoped;
import org.primefaces.component.organigram.OrganigramHelper;
import org.primefaces.model.DefaultOrganigramNode;
import org.primefaces.model.OrganigramNode;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static com.myapp.storing.ConditionType.*;
import static org.apache.commons.lang3.StringUtils.isAllBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * <p>Created by MontolioV on 31.08.18.
 */

@Named
@ViewScoped
public class ItemSearch implements Serializable {
    private static final long serialVersionUID = 6223641177146089455L;
    public static final String ROOT = "ROOT";
    public static final String INTERNAL = "INTERNAL";
    public static final String LEAF = "LEAF";
    @Inject
    private ExternalContext externalContext;
    @Inject
    private QueryPredicateFactory predicateFactory;
    @EJB
    private ItemStore itemStore;
    private List<Item> items;
    private List<Item> filteredItems;
    private List<String> itemTypes;
    private String itemNameParam;
    private String itemOwnerParam;
    private String tagParam;
    private OrganigramNode rootNode;
    private OrganigramNode selectedNode;
    private Condition condition = new Condition(AND);
    private DateInterval conditionDateInterval = new DateInterval();
    private boolean isStringInputRendered;
    private boolean isDateInputRendered;
    private boolean isLikeInputRendered;
    private String viewTimezone;

    @PostConstruct
    public void init(){
        itemTypes = newArrayList(TextItem.class.getSimpleName(), FileItem.class.getSimpleName());
    }

    public void organigramInit() {
        rootNode = new DefaultOrganigramNode(ROOT, new Condition(AND), null);
        rootNode.setDroppable(true);
        rootNode.setSelectable(true);

        if (isAllBlank(itemNameParam, itemOwnerParam, tagParam)) {
            if (externalContext.getUserPrincipal() != null) {
                addLeafNode(new Condition(OWNER, externalContext.getUserPrincipal().getName()), rootNode);
            }
        } else {
            if (isNotBlank(itemNameParam)) {
                addLeafNode(new Condition(NAME, itemNameParam), rootNode);
            }
            if (isNotBlank(itemOwnerParam)) {
                addLeafNode(new Condition(OWNER, itemOwnerParam), rootNode);
            }
            if (isNotBlank(tagParam)) {
                addLeafNode(new Condition(TAG, tagParam), rootNode);
            }
        }
    }

    private void addInternalNode(Condition condition, OrganigramNode parent) {
        DefaultOrganigramNode node = new DefaultOrganigramNode(INTERNAL, condition, parent);
        node.setDraggable(true);
        node.setDroppable(true);
        node.setSelectable(true);
    }

    private void addLeafNode(Condition condition, OrganigramNode parent) {
        DefaultOrganigramNode node = new DefaultOrganigramNode(LEAF, condition, parent);
        node.setDraggable(true);
        node.setCollapsible(false);
        node.setSelectable(true);
    }

    public void resetRoot() {
        rootNode.setChildren(new ArrayList<>());
    }

    public void removeSelectedNode() {
        OrganigramNode node = OrganigramHelper.findTreeNode(rootNode, selectedNode);
        node.getParent().getChildren().remove(node);
    }

    public void addNode() {
        OrganigramNode node = OrganigramHelper.findTreeNode(rootNode, selectedNode);
        ConditionType type = condition.getConditionType();
        if (type.equals(DATE)) {
            condition.setObject(conditionDateInterval);
        }
        conditionDateInterval = new DateInterval();

        if (type.equals(AND) || type.equals(OR) || type.equals(NOT)) {
            addInternalNode(condition, node);
        } else {
            addLeafNode(condition, node);
        }

        condition = new Condition(AND);
        newConditionDialogStateListener();
    }

    public void newConditionDialogStateListener() {
        switch (condition.getConditionType()) {

            case AND:
            case OR:
            case NOT:
                isDateInputRendered = false;
                isLikeInputRendered = false;
                isStringInputRendered = false;
                break;
            case NAME:
            case OWNER:
            case TAG:
                isDateInputRendered = false;
                isLikeInputRendered = true;
                isStringInputRendered = true;
                break;
            case DATE:
                isDateInputRendered = true;
                isLikeInputRendered = false;
                isStringInputRendered = false;
                break;
        }
    }

    public void parseAndRunQuery() {
        if (rootNode.getChildren().isEmpty()) {
            items = itemStore.getAllAccessibleItems();
            return;
        }

        Predicate rootPredicate = makePredicate(rootNode);
        CriteriaQuery<Item> query = predicateFactory.makeItemCriteriaQuery(rootPredicate);
        items = itemStore.executeCustomSelectQuery(query);
    }

    private Predicate makePredicate(OrganigramNode node) {
        Predicate result = null;
        Condition condition = (Condition) node.getData();
        List<OrganigramNode> children = node.getChildren();
        if (children.isEmpty()) {
            switch (condition.getConditionType()) {

                case NAME:
                    if (condition.isLike()) {
                        result = predicateFactory.makeItemNameLikePredicate(((String) condition.getObject()));
                    } else {
                        result = predicateFactory.makeItemNameEqualPredicate(((String) condition.getObject()));
                    }
                    break;
                case OWNER:
                    if (condition.isLike()) {
                        result = predicateFactory.makeItemOwnerLikePredicate(((String) condition.getObject()));
                    } else {
                        result = predicateFactory.makeItemOwnerEqualPredicate(((String) condition.getObject()));
                    }
                    break;
                case TAG:
                    if (condition.isLike()) {
                        result = predicateFactory.makeItemTagLikePredicate((String) condition.getObject());
                    } else {
                        result = predicateFactory.makeItemTagEqualPredicate((String) condition.getObject());
                    }
                    break;
                case DATE:
                    DateInterval dateInterval = (DateInterval) condition.getObject();
                    result = predicateFactory.makeItemCreationDateBetweenPredicate(dateInterval.getFromDate(), dateInterval.getToDate());
                    break;
            }
        } else {
            List<Predicate> subPredicates = children.stream()
                    .map(this::makePredicate)
                    .collect(Collectors.toList());
            switch (condition.getConditionType()) {

                case AND:
                    result = predicateFactory.makeConjunctionPredicate(subPredicates);
                    break;
                case OR:
                    result = predicateFactory.makeDisjunctionPredicate(subPredicates);
                    break;
                case NOT:
                    List<Predicate> invertedSubPredicates = subPredicates.stream()
                            .map(predicateFactory::makeInversionPredicate)
                            .collect(Collectors.toList());
                    result = predicateFactory.makeConjunctionPredicate(invertedSubPredicates);
                    break;
            }
        }

        return result;
    }

    public boolean filterByDate(Object value, Object filter, Locale locale) {
        if (filter == null) {
            return true;
        }
        if (value == null) {
            return false;
        }

        Instant valueInstant = ((Date) value).toInstant();
        Instant filterInstant = ((Date) filter).toInstant();
        Instant filterInstantPlus1Day = filterInstant.plus(1, ChronoUnit.DAYS);
        return (valueInstant.isAfter(filterInstant) && valueInstant.isBefore(filterInstantPlus1Day))
                || valueInstant.equals(filterInstant);
    }

    //Setters & Getters

    public String getROOT() {
        return ROOT;
    }

    public String getINTERNAL() {
        return INTERNAL;
    }

    public String getLEAF() {
        return LEAF;
    }

    public ExternalContext getExternalContext() {
        return externalContext;
    }

    public void setExternalContext(ExternalContext externalContext) {
        this.externalContext = externalContext;
    }

    public QueryPredicateFactory getPredicateFactory() {
        return predicateFactory;
    }

    public void setPredicateFactory(QueryPredicateFactory predicateFactory) {
        this.predicateFactory = predicateFactory;
    }

    public ItemStore getItemStore() {
        return itemStore;
    }

    public void setItemStore(ItemStore itemStore) {
        this.itemStore = itemStore;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<Item> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<Item> filteredItems) {
        this.filteredItems = filteredItems;
    }

    public List<String> getItemTypes() {
        return itemTypes;
    }

    public void setItemTypes(List<String> itemTypes) {
        this.itemTypes = itemTypes;
    }

    public String getItemNameParam() {
        return itemNameParam;
    }

    public void setItemNameParam(String itemNameParam) {
        this.itemNameParam = itemNameParam;
    }

    public String getItemOwnerParam() {
        return itemOwnerParam;
    }

    public void setItemOwnerParam(String itemOwnerParam) {
        this.itemOwnerParam = itemOwnerParam;
    }

    public String getTagParam() {
        return tagParam;
    }

    public void setTagParam(String tagParam) {
        this.tagParam = tagParam;
    }

    public OrganigramNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(OrganigramNode rootNode) {
        this.rootNode = rootNode;
    }

    public OrganigramNode getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(OrganigramNode selectedNode) {
        this.selectedNode = selectedNode;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public boolean isStringInputRendered() {
        return isStringInputRendered;
    }

    public void setStringInputRendered(boolean stringInputRendered) {
        isStringInputRendered = stringInputRendered;
    }

    public boolean isDateInputRendered() {
        return isDateInputRendered;
    }

    public void setDateInputRendered(boolean dateInputRendered) {
        isDateInputRendered = dateInputRendered;
    }

    public boolean isLikeInputRendered() {
        return isLikeInputRendered;
    }

    public void setLikeInputRendered(boolean likeInputRendered) {
        isLikeInputRendered = likeInputRendered;
    }

    public DateInterval getConditionDateInterval() {
        return conditionDateInterval;
    }

    public void setConditionDateInterval(DateInterval conditionDateInterval) {
        this.conditionDateInterval = conditionDateInterval;
    }

    public String getViewTimezone() {
        return viewTimezone;
    }

    public void setViewTimezone(String viewTimezone) {
        this.viewTimezone = viewTimezone;
    }
}
