package engine;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import engine.handler.KeyHandler;
import engine.handler.MouseHandler;

public class InputListener implements KeyListener, MouseListener {
    private static final KeyHandler keyHandler = KeyHandler.getInstance();
    private static final MouseHandler mouseHandler = MouseHandler.getInstance();


    @Override
    public void keyPressed(KeyEvent keyEvent) {
        keyHandler.setKeyPressed(keyEvent);
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        keyHandler.setKeyReleased(keyEvent);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        mouseHandler.setKeyClicked(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseHandler.setKeyPressed(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mouseHandler.setKeyReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseHandler.updateMouseEvent(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseEvent mouseEvent) {
    }
}
