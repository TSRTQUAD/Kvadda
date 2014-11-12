% This function plots all the obtained results and calculates the final
% trajectory length and search area to present.

function [trajectorylength,area,totaltime,velocity] = getResults(object,...
    nodes, trajectory, spiraltrajectory, imagelength_meter, plotresult )

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%     Trajectory length and time calculations      %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Calculate the length of the trajectory and the total time.
[trajectorylength,totaltime,velocity] = getTimeLengthVelocity(trajectory);

if object.mission == 1
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                Coordinate Search                 %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
if plotresult
radiuslength = lldistkm(spiraltrajectory(1,:),spiraltrajectory(end,:))*1e3;
figure('Name','Coordinate Search','Numbertitle','off'); clf; hold on
color_line(trajectory(:,2), trajectory(:,1), velocity(:));
h1 = plot([trajectory(1,2) trajectory(end,2)],...
    [trajectory(1,1) trajectory(end,1)], 'm--');
legend(h1,['Radius: ' num2str(radiuslength) 'm']);
plot_google_map; hold off
end

area = trajectorylength*imagelength_meter;


elseif object.mission == 2
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                   Line Search                    %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
if plotresult
figure('Name','Line Search','Numbertitle','off'); clf; hold on
plot(object.area{1}(:,2),object.area{1}(:,1),'k.');
color_line(trajectory(:,2), trajectory(:,1), velocity(:));
plot_google_map; hold off
end

area = trajectorylength*imagelength_meter;

elseif object.mission == 3
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%                  Area Coverage                   %
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% Plot and calculate the covered area in m^2
earthellipsoid = referenceSphere('earth','m');
earthellipsoidsurfacearea = areaquad(-90,-180,90,180,earthellipsoid);
if plotresult
figure('Name','Optimal trajectory for area coverage','Numbertitle','off')
clf; hold on; area = 0; forbiddenarea = 0;
for ii = 1:length(object.area)
    area = area + areaint(object.area{ii}(:,1),object.area{ii}(:,2))*...
        earthellipsoidsurfacearea;
    h1 = fill(object.area{ii}(:,2),object.area{ii}(:,1),[0.5,0.5,0.5]);
end
for ii = 1:length(object.forbiddenarea)
    forbiddenarea = forbiddenarea+areaint(object.forbiddenarea{ii}(:,1),...
        object.forbiddenarea{ii}(:,2))*earthellipsoidsurfacearea;
    fill(object.forbiddenarea{ii}(:,2),object.forbiddenarea{ii}(:,1),'w');
end
area = area - forbiddenarea;
plot(nodes(:,2),nodes(:,1), '.k');
h2 = color_line(trajectory(:,2), trajectory(:,1), velocity(:));
legend([h1 h2],{['Total area search: ' num2str(area) 'm^2'],...
    ['Total trajectory length: ' num2str(trajectorylength) 'm']})
plot_google_map; hold off
else
area = 0; forbiddenarea = 0;
for ii = 1:length(object.area)
    area = area + areaint(object.area{ii}(:,1),object.area{ii}(:,2))*...
        earthellipsoidsurfacearea;
end
for ii = 1:length(object.forbiddenarea)
    forbiddenarea = forbiddenarea+areaint(object.forbiddenarea{ii}(:,1),...
        object.forbiddenarea{ii}(:,2))*earthellipsoidsurfacearea;
end
area = area - forbiddenarea;
end

end


% --------------------------------------------------
function h = color_line(x, y, c, varargin)
% color_line plots a 2-D "line" with c-data as color
%
%       h = color_line(x, y, c)
%       by default: 'LineStyle','-' and 'Marker','none'
%
%          or
%       h = color_line(x, y, c, mark) 
%          or
%       h = color_line(x, y, c, 'Property','value'...) 
%             with valid 'Property','value' pairs for a surface object
%
%  in:  x      x-data
%       y      y-data
%       c      3rd dimension for colouring
%       mark   for scatter plots with no connecting line
%
% out:  h   handle of the surface object

% (c) Pekka Kumpulainen 
%     www.tut.fi


h = surface(...
  'XData',[x(:) x(:)],...
  'YData',[y(:) y(:)],...
  'ZData',zeros(length(x(:)),2),...
  'CData',[c(:) c(:)],...
  'FaceColor','none',...
  'EdgeColor','flat',...
  'Marker','none');
  
if nargin ==4
    switch varargin{1}
        case {'+' 'o' '*' '.' 'x' 'square' 'diamond' 'v' '^' '>' '<'...
                'pentagram' 'p' 'hexagram' 'h'}
            set(h,'LineStyle','none','Marker',varargin{1})
        otherwise
            error(['Invalid marker: ' varargin{1}])
    end

elseif nargin > 4
    set(h,varargin{:})
end