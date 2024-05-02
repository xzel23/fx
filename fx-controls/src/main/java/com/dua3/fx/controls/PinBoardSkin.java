package com.dua3.fx.controls;

import com.dua3.utility.data.Pair;
import com.dua3.utility.fx.PlatformHelper;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SkinBase;
import javafx.scene.layout.AnchorPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.atomic.AtomicLong;

class PinBoardSkin extends SkinBase<PinBoard> implements Flow.Subscriber<Long> {

    private static final Logger LOG = LogManager.getLogger(PinBoardSkin.class);
    private final AnchorPane pane = new AnchorPane();
    private final ScrollPane scrollPane = new ScrollPane(pane);

    private final SubmissionPublisher<Long> publisher = new SubmissionPublisher<>();
    private Flow.Subscription subscription;
    private AtomicLong changeNr = new AtomicLong();

    PinBoardSkin(PinBoard pinBoard) {
        super(pinBoard);

        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);

        getChildren().setAll(scrollPane);

        pinBoard.areaProperty().addListener((v, o, n) -> {
            pane.setMinWidth(n.getWidth());
            pane.setMinHeight(n.getHeight());
        });

        pinBoard.getItems().addListener((ListChangeListener.Change<?> c) -> refresh());
        pane.layoutBoundsProperty().addListener((o) -> refresh());
        scrollPane.hvalueProperty().addListener((h) -> refresh());
        scrollPane.vvalueProperty().addListener((v) -> refresh());
        scrollPane.widthProperty().addListener((e) -> refresh());
        scrollPane.heightProperty().addListener((e) -> refresh());
        scrollPane.viewportBoundsProperty().addListener((v, o, n) -> refresh());

        // enable/disable refresher
        publisher.subscribe(this);
    }

    private void updateNodes() {
        LOG.trace("updateNodes()");

        PlatformHelper.checkApplicationThread();

        PinBoard board = getSkinnable();

        Rectangle2D viewPort = getViewPort();
        Rectangle2D boardArea = board.getArea();

        double dx = Math.max(0, viewPort.getWidth() - boardArea.getWidth()) / 2.0;
        double dy = Math.max(0, viewPort.getHeight() - boardArea.getHeight()) / 2.0;

        Rectangle2D viewportInLocal = new Rectangle2D(viewPort.getMinX() + boardArea.getMinX(), viewPort.getMinY() + boardArea.getMinY(), viewPort.getWidth(), viewPort.getHeight());

        // populate pane with nodes of visible items
        List<Node> nodes = new ArrayList<>(board.items) // copy list to avoid concurrent modification
                .stream()
                .filter(item -> item.area().intersects(viewportInLocal))
                .map(item -> {
                    LOG.debug("item is visible: {}", item.name());
                    Rectangle2D itemArea = item.area();
                    Node node = item.nodeBuilder().get();
                    node.setTranslateX(dx);
                    node.setTranslateY(dy + itemArea.getMinY());
                    return node;
                })
                .toList();

        pane.setMinWidth(boardArea.getWidth());
        pane.setMinHeight(boardArea.getHeight());
        pane.getChildren().setAll(nodes);
    }

    void refresh() {
        publisher.submit(changeNr.incrementAndGet());
    }

    private Rectangle2D getViewPort() {
        Bounds vpBounds = scrollPane.getViewportBounds();
        return new Rectangle2D(-vpBounds.getMinX(), -vpBounds.getMinY(), vpBounds.getWidth(), vpBounds.getHeight());
    }

    @Override
    public void dispose() {
        publisher.close();
        super.dispose();
    }

    public Pair<Double, Double> getScrollPosition() {
        return Pair.of(scrollPane.getHvalue(), scrollPane.getVvalue());
    }

    public void setScrollPosition(double hValue, double vValue) {
        scrollPane.setHvalue(hValue);
        scrollPane.setVvalue(vValue);
    }

    /**
     * Get Item at point and coordinates relative to item.
     *
     * @param xViewport x-coordinate (relative to board)
     * @param yViewport y-coordinate (relative to board)
     * @return Optional containing the item at (x,y) and the coordinates relative to the item area
     */
    public Optional<PinBoard.PositionInItem> getPositionInItem(double xViewport, double yViewport) {
        Rectangle2D vp = getViewPort();
        double x = xViewport + vp.getMinX();
        double y = yViewport + vp.getMinY();
        Rectangle2D b = getSkinnable().getArea();
        List<PinBoard.Item> items = new ArrayList<>(getSkinnable().getItems());
        for (PinBoard.Item item : items) {
            Rectangle2D a = item.area();
            if (a.contains(x, y)) {
                return Optional.of(new PinBoard.PositionInItem(item, x + b.getMinX() - a.getMinX(), y + b.getMinY() - a.getMinY()));
            }
        }
        return Optional.empty();
    }

    @Override
    public void onSubscribe(Flow.Subscription subscription) {
        LOG.debug("onSubScribe");
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(Long nr) {
        LOG.debug("onNext({})", nr);
        PlatformHelper.runAndWait(this::updateNodes);
        subscription.request(1);
    }

    @Override
    public void onError(Throwable throwable) {
        LOG.warn("onError()", throwable);
    }

    @Override
    public void onComplete() {
        LOG.debug("onComplete({})");
    }
}
