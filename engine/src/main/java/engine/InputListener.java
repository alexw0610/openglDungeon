package engine;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import engine.handler.key.KeyHandler;

public class InputListener implements KeyListener, MouseListener {
    KeyHandler keyHandler = KeyHandler.KEY_HANDLER;

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

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseWheelMoved(MouseEvent mouseEvent) {
        keyHandler.setMouseWheelRotation(mouseEvent);
    }
}
